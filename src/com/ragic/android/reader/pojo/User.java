package com.ragic.android.reader.pojo;

import java.util.ArrayList;

/**
 * Author: Azuritul
 * Date:   2010/10/5
 * Time:   上午 11:31:56
 */
public class User {

    private String email;
    private String password;
    private ArrayList<Account> accounts;

    public User(){
        
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void addAccount(String accountName){
        if(accounts == null){
           accounts = new ArrayList<Account>();
        }
        accounts.add(new Account(accountName));
    }

    public String[] getAccountsAsArray(){
        String[] temp = {};
        if(accounts != null){
            temp = new String[accounts.size()];
            int count = 0;
            for(Account a : accounts) {
                temp[count] = a.getAccountName();
                count++;
            }
        }
        return temp;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
