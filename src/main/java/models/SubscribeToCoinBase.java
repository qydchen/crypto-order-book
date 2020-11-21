package models;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

public class SubscribeToCoinBase {
    @Setter
    @Getter
    private String type;

    @Setter
    @Getter
    private List<String> product_ids;

    @Setter
    @Getter
    private List<String> channels;

    public SubscribeToCoinBase(List<String> product_ids) {
        this.type = "subscribe";
        this.channels = Arrays.asList("level2");
        this.product_ids = product_ids;
    }
}
