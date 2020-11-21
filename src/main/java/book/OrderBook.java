package book;

import models.L2Update;
import models.Snapshot;
import utils.JSONUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OrderBook {
    final int depth = 10;
    private List<List<String>> asks;
    private List<List<String>> bids;

    public void receiveTick(String tick) {
        String type = JSONUtils.getType(tick);
        this.handleTick(type, tick);
    }

    public void handleTick(String type, String tick) {
        switch (type) {
            case ("subscriptions"): {
                System.out.println(tick);
                break;
            }
            case ("snapshot"): {
                Snapshot snapshot = JSONUtils.toSnapshot(tick);
                this.asks = snapshot.getAsks().subList(0, 15);
                Collections.reverse(this.asks);
                this.bids = snapshot.getBids().subList(0, 15);
                this.print();
                break;
            }
            case ("l2update"): {
                L2Update l2Update = JSONUtils.toL2Update(tick);
                for (int i = 0; i < l2Update.getChanges().size(); i++) {
                    List<String> change = l2Update.getChanges().get(i);
                    this.update(change);
                }
                this.print();
                break;
            }
            default: {
                break;
            }
        }
    }

    private void print() {
        final String delimiter = "   |   ";
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(String.join(delimiter, Arrays.asList("Price  ", "  Size")));
        this.asks.subList(0, depth).forEach(t -> System.out.println(String.join(delimiter, t)));
        System.out.println("Asks ^------------v Bids");
        this.bids.subList(0,depth).forEach(t -> System.out.println(String.join(delimiter, t)));
    }

    private void update(List<String> change) {
        String side = change.get(0);
        BigDecimal changePrice = new BigDecimal(change.get(1));
        BigDecimal changeSize = new BigDecimal(change.get(2));
        List<List<String>> book = side == "buy" ? this.bids : this.asks;
        for (int i = 0; i < book.size(); i++) {
            List<String> currentOrder = book.get(i);
            BigDecimal currentPrice = new BigDecimal(currentOrder.get(0));
            if (changePrice.compareTo(currentPrice) == 0) {
                if (changeSize.compareTo(BigDecimal.ZERO) == 0) {
//                    System.out.println(String.format("remove: %s", i));
                    book.remove(i);
                } else {
//                    System.out.println(String.format("set: %s", i));
                    book.set(i, change.subList(1, 3));
                }
            }
        }
        // If no updates on the current book, then it must be an insertion;
        if (changeSize.compareTo(BigDecimal.ZERO) == 0) return;
        if (side == "buy") {
            book.add(change.subList(1, 3));
        } else {
            book.add(0, change.subList(1, 3));
        }
    }
}
