package com.lnwazg;

import java.util.List;
import java.util.Map;

import com.lnwazg.api.DistributedTask;
import com.lnwazg.bean.HandleResult;

/**
 * 第0个节点从1计数到10000，第1个节点从10001计数到20000，以此类推，最终上送总和数据
 * @author nan.li
 * @version 2017年7月14日
 */
public class Task003 extends DistributedTask
{
    @Override
    public void executeCustom(Map<String, Object> map)
    {
        //汇总计算结果
        int sum = 0;
        int start = nodeNum * 100 + 1;//1    101
        int end = (nodeNum + 1) * 100;//100  200
        for (int i = start; i <= end; i++)
        {
            sum += i;
        }
        //上报计算结果
        report("sum", sum);
    }
    
    public String getTaskDescription()
    {
        return "汇总计数";
    }
    
    @Override
    public HandleResult reducer(Map<String, List<HandleResult>> handleResultsMap)
    {
        int sum = 0;
        for (String nodeNum : handleResultsMap.keySet())
        {
            List<HandleResult> handleResults = handleResultsMap.get(nodeNum);
            //因为只有1步上送，因此只要取出第1步的上送结果即可！
            sum += Integer.valueOf(handleResults.get(0).getParamMap().get("sum"));
        }
        return new HandleResult().setResult(sum);
    }
}
