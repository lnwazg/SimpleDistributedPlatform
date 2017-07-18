package com.lnwazg.kit.lambda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.lnwazg.kit.lambda.Transaction.TransactionType;
import com.lnwazg.kit.list.Lists;
import com.lnwazg.kit.testframework.TF;
import com.lnwazg.kit.testframework.anno.TestCase;

public class Test
{
    List<Transaction> transactions = Lists.asList(new Transaction(TransactionType.GROCERY, 100, 1),
        new Transaction(TransactionType.SUPERMARKET, 85, 2),
        new Transaction(TransactionType.CONVENIENT, 25, 3),
        new Transaction(TransactionType.GROCERY, 67, 4));
    
    public static void main(String[] args)
    {
        TF.l(Test.class);
    }
    
    @TestCase
    void test1()
    {
        List<Transaction> groceryTransactions = new ArrayList<>();
        for (Transaction t : transactions)
        {
            if (t.getType() == TransactionType.GROCERY)
            {
                groceryTransactions.add(t);
            }
        }
        Collections.sort(groceryTransactions, new Comparator<Transaction>()
        {
            public int compare(Transaction t1, Transaction t2)
            {
                return t1.getValue() - t2.getValue();
            }
        });
        List<Integer> transactionIds = new ArrayList<>();
        for (Transaction t : groceryTransactions)
        {
            transactionIds.add(t.getId());
        }
        System.out.println(transactionIds);
    }
    
    @TestCase
    void test2()
    {
        List<Integer> transactionIds = transactions.parallelStream()
            .filter(t -> t.getType() == TransactionType.GROCERY)
            .sorted(Comparator.comparing(Transaction::getValue))
            .map(Transaction::getId)
            .collect(Collectors.toList());
        System.out.println(transactionIds);
    }
    
}
