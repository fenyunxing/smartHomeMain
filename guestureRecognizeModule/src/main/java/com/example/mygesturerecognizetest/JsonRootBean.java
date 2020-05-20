/**
  * Copyright 2020 bejson.com 
  */
package com.example.mygesturerecognizetest;
import java.util.List;

/**
 * Auto-generated: 2020-05-19 16:25:9
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class JsonRootBean {

    private long log_id;
    private int result_num;
    private List<Result> result;
    public void setLog_id(long log_id) {
         this.log_id = log_id;
     }
     public long getLog_id() {
         return log_id;
     }

    public void setResult_num(int result_num) {
         this.result_num = result_num;
     }
     public int getResult_num() {
         return result_num;
     }

    public void setResult(List<Result> result) {
         this.result = result;
     }
     public List<Result> getResult() {
         return result;
     }

}