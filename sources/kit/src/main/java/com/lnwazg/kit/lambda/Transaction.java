package com.lnwazg.kit.lambda;

public class Transaction
{
    private TransactionType type;
    
    private int value;
    
    private int id;
    
    public TransactionType getType()
    {
        return type;
    }
    
    public void setType(TransactionType type)
    {
        this.type = type;
    }
    
    public int getValue()
    {
        return value;
    }
    
    public void setValue(int value)
    {
        this.value = value;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public Transaction(TransactionType type, int value, int id)
    {
        super();
        this.type = type;
        this.value = value;
        this.id = id;
    }
    
    @Override
    public String toString()
    {
        return "Transaction [type=" + type + ", value=" + value + ", id=" + id + "]";
    }
    
    public static enum TransactionType
    {
        GROCERY, SUPERMARKET, CONVENIENT
    }
}
