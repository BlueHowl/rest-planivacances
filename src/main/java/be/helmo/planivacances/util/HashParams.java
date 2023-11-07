package be.helmo.planivacances.util;

public class HashParams {

    private String algorithm;
    private String base64_signer_key;
    private String base64_salt_separator;
    private int rounds;
    private int mem_cost;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getBase64_signer_key() {
        return base64_signer_key;
    }

    public void setBase64_signer_key(String base64_signer_key) {
        this.base64_signer_key = base64_signer_key;
    }

    public String getBase64_salt_separator() {
        return base64_salt_separator;
    }

    public void setBase64_salt_separator(String base64_salt_separator) {
        this.base64_salt_separator = base64_salt_separator;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int getMem_cost() {
        return mem_cost;
    }

    public void setMem_cost(int mem_cost) {
        this.mem_cost = mem_cost;
    }

}
