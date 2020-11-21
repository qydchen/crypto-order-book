package models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class L2Update {

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private String product_id;

    @Getter
    @Setter
    private String time;

    @Getter
    @Setter
    private List<List<String>> changes;
}
