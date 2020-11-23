package book;

import models.L2Update;
import models.Snapshot;
import utils.JSONUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class OrderBook {
    final int depth = 10;
    final int buffer = 50;
    private List<List<String>> asks;
    private List<List<String>> bids;
    public void receiveTick(String tick) {
        String type = JSONUtils.getType(tick);
        switch (type) {
            case ("snapshot"): {
                Snapshot snapshot = JSONUtils.toSnapshot(tick);
                this.asks = snapshot.getAsks().subList(0, depth + buffer);
                this.bids = snapshot.getBids().subList(0, depth + buffer);
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

    private void update(List<String> change) {
        String side = change.get(0);
        BigDecimal changePrice = new BigDecimal(change.get(1));
        BigDecimal changeSize = new BigDecimal(change.get(2));
        List<List<String>> book = side.equals("buy") ? this.bids : this.asks;
        for (int i = 0; i < book.size(); i++) {
            List<String> currentOrder = book.get(i);
            BigDecimal currentPrice = new BigDecimal(currentOrder.get(0));
            if (changePrice.compareTo(currentPrice) == 0) {
                if (changeSize.compareTo(BigDecimal.ZERO) == 0) {
                    book.remove(i);
                    return;
                } else {
                    book.set(i, change.subList(1, 3));
                    return;
                }
            } else if (changeSize.compareTo(BigDecimal.ZERO) != 0) {
                if ((side.equals("buy") && changePrice.compareTo(currentPrice) == 1) ||
                        (side.equals("sell") && changePrice.compareTo(currentPrice) == -1)) {
                    book.add(i, change.subList(1, 3));
                    return;
                } else if (book.size() < depth + buffer) {
                    book.add(change.subList(1, 3));
                    return;
                }
            }
        }
    }

    private void print() {
        final String delimiter = "   |   ";
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(String.join(delimiter, Arrays.asList("Price  ", "  Size")));
        List<List<String>> asks = this.asks.subList(0, depth);
        for (int i = asks.size() - 1; i >= 0; i--) {
            System.out.println(String.join(delimiter, asks.get(i)));
        }
        System.out.println("Asks ^------------v Bids");
        this.bids.subList(0, depth).forEach(t -> System.out.println(String.join(delimiter, t)));
    }
}
