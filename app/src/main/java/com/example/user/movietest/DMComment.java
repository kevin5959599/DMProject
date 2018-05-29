package com.example.user.movietest;

/**
 * Created by jt3282 on 2018/5/26.
 */

public class DMComment {
    static void uploadComment(final String COMMENT_URL, final String account,final String dmtype, final String dmname, final String comment){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = DownloadImg.executeQuery(COMMENT_URL,"INSERT INTO dm_comment(account" +
                        ",dmtype,dmname,user_comment,comment_time) VALUE('"+account+"','"+dmtype+"','"+dmname+"','"+comment+"','");
            }
        }).start();
    }
    static void updateComment(final String COMMENT_URL, final String account,final String dmtype, final String dmname, final String comment,final String time){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = DownloadImg.executeQuery(COMMENT_URL,"UPDATE dm_comment SET user_comment='"+comment+"' "
                        +"WHERE account='"+account+"' AND dmtype='"+dmtype+"' AND dmname='"+dmname+"' AND comment_time='"+time+"'");

            }
        }).start();
    }
    static void deleteComment(final String COMMENT_URL, final String account,final String dmtype, final String dmname, final String comment,final String time){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = DownloadImg.executeQuery(COMMENT_URL,"DELETE FROM dm_comment "
                        +"WHERE account='"+account+"' AND dmtype='"+dmtype+"' AND dmname='"+dmname+"' AND user_comment='"+comment+"' AND comment_time='"+time+"'");

            }
        }).start();
    }
    static void uploadRate(final String RATE_URL, final String account,final String dmtype, final String dmname, final String cast
                           ,final String plot,final String se,final String ad,final String rd,final String average_score){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = DownloadImg.executeQuery(RATE_URL,"INSERT INTO dm_score(account" +
                        ",dmtype,dmname,cast,plot,se,ad,rd,average_score) VALUE('"+account+"','"+dmtype+"','"+dmname+"','"+cast+"','"
                        +plot+"','"+se+"','"+ad+"','"+rd+"','"+average_score+"')");
            }
        }).start();
    }
    static void updateRate(final String RATE_URL, final String account,final String dmtype, final String dmname, final String cast
            ,final String plot,final String se,final String ad,final String rd,final String average_score){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = DownloadImg.executeQuery(RATE_URL,"UPDATE dm_score SET cast='"
                        +cast+"',plot='"+plot+"',se='"+se+"',ad='"+ad+"',rd='"+rd+"',average_score='"+
                        average_score+"' WHERE account='"+account+"' AND dmtype='"+dmtype+
                                "' AND dmname='"+dmname+"'");

            }
        }).start();
    }
    static void uploadReport(final String REPORT_URL, final String account,final String dmtype, final String dmname, final String comment, final String commnet_time,final String reason){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = DownloadImg.executeQuery(REPORT_URL,"INSERT INTO dm_report(account" +
                        ",dmtype,dmname,user_comment,comment_time,reason) VALUE('"+account+"','"+dmtype+"','"+dmname+"','"+comment+"','"+commnet_time+"','"+reason+"')");
            }
        }).start();
    }
}
