package com.ragic.android.reader.pojo;

/**
 * Author: Azuritul
 * Date:   2010/10/5
 * Time:   下午 01:18:02
 */
public class Account {
    
    private String accountName;

    public Account(String accountName){
        this.accountName = accountName;
    }
    
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (accountName != null ? !accountName.equals(account.accountName) : account.accountName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return accountName != null ? accountName.hashCode() : 0;
    }
}
