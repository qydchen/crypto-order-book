package book;

import models.L2Update;
import models.Snapshot;
import utils.JSONUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OrderBook {
    final int depth = 10;
    final String delimiter = "   |   ";
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
                this.asks = snapshot.getAsks();
                this.bids = snapshot.getBids();
                this.print();
                break;
            }
            case ("l2update"): {
                L2Update l2Update = JSONUtils.toL2Update(tick);
//                Subsequent updates will have the type l2update.
//                The changes property of l2updates is an array with [side, price, size] tuples.
//                The time property of l2update is the time of the event as recorded by our trading engine.
//                Please note that size is the updated size at that price level, not a delta.
//                A size of "0" indicates the price level can be removed.
                for (int i = 0; i < l2Update.getChanges().size(); i++) {
                    List<String> change = l2Update.getChanges().get(i);
                    String side = change.get(0);
                    String price = change.get(1);
                    String size = change.get(2);
                }
//                System.out.println(tick);
                break;
            }
            default: {
                break;
            }
        }
    }

    private void print() {
        System.out.println(String.join(this.delimiter, Arrays.asList("Bid                    ", "Ask")));
        System.out.println(String.join(this.delimiter, Arrays.asList("Price ", "Size      ", "Price ", "Size")));

        List<List<String>> merged = this.merge(this.bids.subList(0, this.depth), this.asks.subList(0, this.depth));
        merged.forEach(t -> System.out.println(String.join(this.delimiter, t)));
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

}
