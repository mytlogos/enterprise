package Enterprise.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 */
public class stubdao {
    public static void main(String[] args) throws SQLException {
        String html = "<select id=\"archives-dropdown-2\" name=\"archive-dropdown\"\n" +
                "        onchange=\"document.location.href=this.options[this.selectedIndex].value;\">\n" +
                "    <option value=\"\">Select Month</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2017/09\"> September 2017 &nbsp;(2)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2017/08\"> August 2017 &nbsp;(10)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2017/07\"> July 2017 &nbsp;(13)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2017/06\"> June 2017 &nbsp;(22)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2017/05\"> May 2017 &nbsp;(15)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2017/04\"> April 2017 &nbsp;(18)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2017/03\"> March 2017 &nbsp;(17)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2017/02\"> February 2017 &nbsp;(15)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2017/01\"> January 2017 &nbsp;(15)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2016/12\"> December 2016 &nbsp;(17)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2016/11\"> November 2016 &nbsp;(16)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2016/10\"> October 2016 &nbsp;(18)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2016/09\"> September 2016 &nbsp;(28)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2016/08\"> August 2016 &nbsp;(18)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2016/07\"> July 2016 &nbsp;(22)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2016/06\"> June 2016 &nbsp;(24)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2016/05\"> May 2016 &nbsp;(16)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2016/04\"> April 2016 &nbsp;(18)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2016/03\"> March 2016 &nbsp;(25)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2016/02\"> February 2016 &nbsp;(24)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2016/01\"> January 2016 &nbsp;(24)</option>\n" +
                "    <option value=\"http://tseirptranslations.com/2015/12\"> December 2015 &nbsp;(9)</option>\n" +
                "</select>";


    }

    private static Connection getCon() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:enterprise.db");
    }
}
