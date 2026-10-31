package ootie.game;

import lombok.Data;

@Data
public class TradeNode {
    private String tradeNodeName;
    private String additionalRequirements;
    private KeyProvince keyProvince1;
    private KeyProvince keyProvince2;
    private KeyProvince keyProvince3;
    private KeyProvince keyProvince4;
}
