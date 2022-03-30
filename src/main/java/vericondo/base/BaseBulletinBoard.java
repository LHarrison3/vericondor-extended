package vericondo;

import java.util.*;
import java.math.BigInteger;

/**
This class represents the bulletin board.
*/
public class BaseBulletinBoard extends BulletinBoard {

    // the bulletin board holds receipts for each ballot
    private HashMap<Integer, BaseReceipt> receipts;


    public BaseBulletinBoard(int num_candidates, BigInteger g0, BigInteger g1,
        BigInteger p, BigInteger q) {

        super(num_candidates, g0, g1, p, q);
        this.receipts = new HashMap<Integer, BaseReceipt>();

    }

    // add a receipt to the bulletin board
    public void addReceipt(int voter, BaseReceipt receipt) {
        this.receipts.put(voter, receipt);
    }

    // get the receipt for a given voter
    public BaseReceipt getReceipt(int voter) {
        return this.receipts.get(voter);
    }

    // get receipts of all ballots
    public HashMap<Integer, BaseReceipt> getReceipts() {
        return this.receipts;
    }

    // get receipts of all audited ballots
    public ArrayList<BaseReceipt> getAuditedReceipts() {
        ArrayList<BaseReceipt> audited_receipts = new ArrayList<BaseReceipt>();
        for (HashMap.Entry<Integer, BaseReceipt> entry : receipts.entrySet()) {
            BaseReceipt receipt = entry.getValue();
            if (receipt instanceof AuditedBaseReceipt) {
                audited_receipts.add(receipt);
            }
        }
        return audited_receipts;
    }

    // get receipts of all confirmed ballots
    public ArrayList<BaseReceipt> getConfirmedReceipts() {
        ArrayList<BaseReceipt> confirmed_receipts = new ArrayList<BaseReceipt>();
        for (HashMap.Entry<Integer, BaseReceipt> entry : receipts.entrySet()) {
            BaseReceipt receipt = entry.getValue();
            if (receipt instanceof ConfirmedBaseReceipt) {
                confirmed_receipts.add(receipt);
            }
        }
        return confirmed_receipts;
    }

    // display a particular receipt
    public void displayReceipt(int voter) {
        receipts.get(voter).display();
    }

    // display all receipts
    public void displayAllReceipts() {
        for (HashMap.Entry<Integer, BaseReceipt> entry : receipts.entrySet()) {
            BaseReceipt receipt = entry.getValue();
            receipt.display();
        }
    }

    // display the entire bulletin board
    @Override
    public void display() {
        super.display();
        displayAllReceipts();
    }

}
