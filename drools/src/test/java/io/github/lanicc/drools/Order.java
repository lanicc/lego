package io.github.lanicc.drools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 2024/9/13.
 *
 * @author lan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private Integer price;

    private Integer score;

}
