package vericondo;

import java.util.*;
import java.math.BigInteger;

/**
This class represents the bulletin board.
*/
public class PrefRelBulletinBoard extends BulletinBoard {

    // the bulletin board holds receipts for each ballot
    private HashMap<Integer, PrefRelReceipt> receipts;


    public PrefRelBulletinBoard(int num_candidates, BigInteger g0, BigInteger g1,
        BigInteger p, BigInteger q) {

        super(num_candidates, g0, g1, p, q);
        this.receipts = new HashMap<Integer, PrefRelReceipt>();

    }

    // add a receipt to the bulletin board
    public void addReceipt(int voter, PrefRelReceipt receipt) {
        this.receipts.put(voter, receipt);
    }

    // get the receipt for a given voter
    public PrefRelReceipt getReceipt(int voter) {
        return this.receipts.get(voter);
    }

    // get receipts of all ballots
    public HashMap<Integer, PrefRelReceipt> getReceipts() {
        return this.receipts;
    }

    // get receipts of all audited ballots
    public ArrayList<PrefRelReceipt> getAuditedReceipts() {
        ArrayList<PrefRelReceipt> audited_receipts = new ArrayList<PrefRelReceipt>();
        for (HashMap.Entry<Integer, PrefRelReceipt> entry : receipts.entrySet()) {
            PrefRelReceipt receipt = entry.getValue();
            if (receipt instanceof AuditedPrefRelReceipt) {
                audited_receipts.add(receipt);
            }
        }
        return audited_receipts;
    }

    // get receipts of all confirmed ballots
    public ArrayList<PrefRelReceipt> getConfirmedReceipts() {
        ArrayList<PrefRelReceipt> confirmed_receipts = new ArrayList<PrefRelReceipt>();
        for (HashMap.Entry<Integer, PrefRelReceipt> entry : receipts.entrySet()) {
            PrefRelReceipt receipt = entry.getValue();
            if (receipt instanceof ConfirmedPrefRelReceipt) {
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
        for (HashMap.Entry<Integer, PrefRelReceipt> entry : receipts.entrySet()) {
            PrefRelReceipt receipt = entry.getValue();
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
