package book;

import models.L2Update;
import models.Snapshot;
import utils.JSONUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
                this.asks = snapshot.getAsks().subList(0, 10);
                this.bids = snapshot.getBids().subList(0, 10);
                this.print();
                break;
            }
            case ("l2update"): {
                L2Update l2Update = JSONUtils.toL2Update(tick);
                for (int i = 0; i < l2Update.getChanges().size(); i++) {
                    List<String> change = l2Update.getChanges().get(i);
                    this.update(change);
                }
//                this.print();
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
        System.out.println(String.join(delimiter, Arrays.asList("Bid                    ", "Ask")));
        System.out.println(String.join(delimiter, Arrays.asList("Price ", "Size      ", "Price ", "Size")));
        List<List<String>> merged = this.merge(this.bids.subList(0, this.depth), this.asks.subList(0, this.depth));
        merged.forEach(t -> System.out.println(String.join(delimiter, t)));
    }

    private List<List<String>> merge(List<List<String>> bids, List<List<String>> asks) {
        List<List<String>> merged = new ArrayList<>();
        for (int i = 0; i < bids.size(); i++) {
            List<String> row = new ArrayList(bids.get(i));
            row.addAll(asks.get(i));
            merged.add(row);
        }
        return merged;
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
        book.add(change.subList(1, 3));
    }
}
