package vericondo;

import java.util.*;
import java.math.BigInteger;

/**
This class represents the bulletin board.
*/
public class IndiffBulletinBoard extends BulletinBoard {

    // the bulletin board holds receipts for each ballot
    private HashMap<Integer, IndiffReceipt> receipts;


    public IndiffBulletinBoard(int num_candidates, BigInteger g0, BigInteger g1,
        BigInteger p, BigInteger q) {

        super(num_candidates, g0, g1, p, q);
        this.receipts = new HashMap<Integer, IndiffReceipt>();

    }

    // add a receipt to the bulletin board
    public void addReceipt(int voter, IndiffReceipt receipt) {
        this.receipts.put(voter, receipt);
    }

    // get the receipt for a given voter
    public IndiffReceipt getReceipt(int voter) {
        return this.receipts.get(voter);
    }

    // get receipts of all ballots
    public HashMap<Integer, IndiffReceipt> getReceipts() {
        return this.receipts;
    }

    // get receipts of all audited ballots
    public ArrayList<IndiffReceipt> getAuditedReceipts() {
        ArrayList<IndiffReceipt> audited_receipts = new ArrayList<IndiffReceipt>();
        for (HashMap.Entry<Integer, IndiffReceipt> entry : receipts.entrySet()) {
            IndiffReceipt receipt = entry.getValue();
            if (receipt instanceof AuditedIndiffReceipt) {
                audited_receipts.add(receipt);
            }
        }
        return audited_receipts;
    }

    // get receipts of all confirmed ballots
    public ArrayList<IndiffReceipt> getConfirmedReceipts() {
        ArrayList<IndiffReceipt> confirmed_receipts = new ArrayList<IndiffReceipt>();
        for (HashMap.Entry<Integer, IndiffReceipt> entry : receipts.entrySet()) {
            IndiffReceipt receipt = entry.getValue();
            if (receipt instanceof ConfirmedIndiffReceipt) {
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
        for (HashMap.Entry<Integer, IndiffReceipt> entry : receipts.entrySet()) {
            IndiffReceipt receipt = entry.getValue();
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
