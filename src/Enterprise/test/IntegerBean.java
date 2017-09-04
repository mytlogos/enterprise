package Enterprise.test;

import scrape.sources.novels.ParseTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dominik on 08.07.2017.
 * Class for testing purposes.
 */
public class IntegerBean implements Serializable {

    private List<String> times = new ArrayList<>();

    public static void main(String[] args) {
        IntegerBean integerBean = new IntegerBean();
        integerBean.addTimes();
        System.out.println("Now parsing...");
        System.out.println();
        ParseTime time = new ParseTime();
        integerBean.times.forEach(s -> System.out.println(ParseTime.parseTime(s)));
        System.out.println(integerBean.times.size());
    }

    private List<String> addTimes() {
        times.add("2017-09-01T23:42:00+00:00");
        times.add("2017-08-04T05:15:54+00:00");
        times.add("2017-08-27T15:48:33+00:00");
        times.add("2017-09-01T07:10:52+00:00");
        times.add("2017-09-01T09:36:49+00:00");
        times.add("2017-09-02T01:49:33+00:00");
        times.add("2017-05-01T18:13:13+00:00");
        times.add("2017-07-25T02:06:44+00:00");
        times.add("2017-09-02T15:44:28+00:00");
        times.add("2017-09-02T13:23:18+00:00");
        times.add("2017-08-28T14:10:01+00:00");
        times.add("2017-08-27T09:57:00+00:00");
        times.add("2017-09-02T11:43:16+00:00");
        times.add("2017-08-03T16:30:27+00:00");
        times.add("2017-09-03T02:34:40+00:00");
        times.add("2017-08-16T11:20:35+00:00");
        times.add("2017-08-11T14:40:39+00:00");
        times.add("2017-05-18T19:48:33+00:00");
        times.add("2017-09-02T23:00:00+08:00");
        times.add("2016-11-14T16:13:26+00:00");
        times.add("2017-08-01T09:42:33+00:00");
        times.add("2017-09-02T10:45:38+00:00");
        times.add("2017-09-02T20:12:24+00:00");
        times.add("2017-02-19T22:58:00+08:00");
        times.add("2017-09-02T08:30:07+00:00");
        times.add("2015-12-25T15:05:31+00:00");
        times.add("2016-01-21T08:31:46+00:00");
        times.add("2016-06-26T17:14:00+05:30");
        times.add("2017-09-02T20:35:30+00:00");
        times.add("2016-01-13T02:21:35+00:00");
        times.add("2017-08-31T20:29:48+00:00");
        times.add("2017-09-02T16:52:35+00:00");
        times.add("2017-06-07T16:04:03+00:00");
        times.add("2017-08-31T00:42:49+00:00");
        times.add("2017-03-13T04:26:52+00:00");
        times.add("2017-06-16T08:16:31+00:00");
        times.add("2017-03-28T18:38:23+00:00");
        times.add("2017-09-03T00:26:14+00:00");
        times.add("2017-07-11T19:00:31+00:00");
        times.add("2017-08-28T11:15:51+00:00");
        times.add("2017-02-13T11:36:32+00:00");
        times.add("2017-08-29T20:00:08+00:00");


        times.add("June 28, 2016");
        times.add("July 12, 2017");
        times.add("August 23, 2017");
        times.add("July 19, 2017");
        times.add("September 3, 2017");
        times.add("November 8, 2016");
        times.add("May 13, 2015");
        times.add("September 1, 2017");
        times.add("April 29, 2017");
        times.add("June 29, 2017");
        times.add("August 31, 2017");
        times.add("June 5, 2017");
        times.add("March 27, 2017");
        times.add("August 31, 2017");
        times.add("March 16, 2016");

        times.add("Saturday, September 2, 2017");

        times.add("2017-08-21");
        times.add("2017-03-20");
        times.add("2017-04-30");

        times.add("1504415349");
        times.add("1504413657");

        times.add("02.09.2017");

        times.add("26 minutes ago");
        times.add("3 hours ago");
        times.add("Posted 59 minutes ago");

        times.add("August 31, 2017 at 06:23 pm (UTC -5)");

        times.add("2017-09-02 14:31:40");
        times.add("2017-09-03 05:51");

        times.add("4:05 am (+02:00) on September 1, 2017");

        return times;
    }

}
