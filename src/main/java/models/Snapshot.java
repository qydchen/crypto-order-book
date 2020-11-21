package models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Snapshot {

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private String product_id;

    @Getter
    @Setter
    private List<List<String>> bids;

    @Getter
    @Setter
    private List<List<String>> asks;
}
