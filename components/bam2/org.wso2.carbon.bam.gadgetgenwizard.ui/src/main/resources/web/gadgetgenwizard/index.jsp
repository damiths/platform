<!--
~ Copyright WSO2, Inc. (http://wso2.com)
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~ http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
-->

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>
<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.wso2.carbon.CarbonConstants" %>

<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>

<script type="text/javascript" src="../admin/js/breadcrumbs.js"></script>
<script type="text/javascript" src="../admin/js/cookies.js"></script>
<script type="text/javascript" src="../admin/js/main.js"></script>


<link type="text/css" href="../dialog/js/jqueryui/tabs/ui.all.css" rel="stylesheet"/>
<script type="text/javascript" src="../dialog/js/jqueryui/tabs/jquery-1.2.6.min.js"></script>
<script type="text/javascript"
        src="../dialog/js/jqueryui/tabs/jquery-ui-1.6.custom.min.js"></script>
<script type="text/javascript" src="../dialog/js/jqueryui/tabs/jquery.cookie.js"></script>
<script type="text/javascript">


    $(document).ready(function () {

        if ($("#page").val() == "1") {
            $("#back").attr("disabled", "disabled");
        }

        $("#generate").hide();

        $("#generate").click(function() {
            sendAjaxRequest("generate_gadget_ajaxprocessor.jsp");
        })

        $("#back").click(function() {
            var backURL = "";
            if ($("#page").val() == "2") {
                backURL = "datasource_ajaxprocessor.jsp";
            } else if ($("#page").val() == "3") {
                backURL = "sqlinput_ajaxprocessor.jsp";
            } else if ($("#page").val() == "4") {
                backURL = "pickuielement_ajaxprocessor.jsp";
            } else if ($("#page").val() == "5") {
                backURL = "preview_ajaxprocessor.jsp";
            }

            sendAjaxRequest(backURL);

        })

        $("#validate").click(function() {
            $.post("validate_db_conn.jsp", $(form)function(html) {
                var success = (html.toLowerCase().match(/success/));
                if (success) {
                    CARBON.showInfoDialog(html);
                } else {
                    CARBON.showErrorDialog(html);
                }
            })

        })

        function changeBackBtnState() {
            if ($("#page").val() == "01") {
                $("#back").attr("disabled", "disabled");
            } else {
                $("#back").removeAttr('disabled');
            }
        }


        function changeGenBtnState() {
            if ($("#page").val() == "04") {
                $("#generate").show();
            } else {
                $("#generate").hide();
            }
        }

        function changeNxtBtnState() {
            if (parseInt($("#page").val()) >= 4) {
                $("#next").hide();
            } else {
                $("#next").show();
            }
        }

        function sendAjaxRequest(url, data) {
            if (typeof data == 'undefined' || data == null) {
                data = $("form").serialize();
            }
            //start the ajax
            $.ajax({
                //this is the php file that processes the data and send mail
                url: url,

                //GET method is used
                type: "POST",

                //pass the data
                data: data,

                //Do not cache the page
                cache: false,

                //success
                success: function (html) {
                    //if process.php returned 1/true (send mail success)
                    //hide the form
                    $('#div-form').fadeOut('fast', function() {

                        $('#change-area').html(html);

                        //show the success message
                        $('#change-area').fadeIn('fast');

                        changeHeading(parseInt($("#page").val()));

                        changeBackBtnState();
                        changeGenBtnState();
                        changeNxtBtnState();
                    });
                }
            });
        }

        var wizardPgTitle = ["Data Source", "SQL Queries", "UI Elements", "Gadget", "Done!"];

        function changeHeading(pageNo) {
            var stepTitle = "Step " + pageNo + " of 5 : ";
            $("#page-title").html(wizardPgTitle[pageNo]);
            $("#step-title").html(stepTitle + wizardPgTitle[pageNo]);

        }

        $("#next").click(function() {
            var jdbcurl = $("[name=jdbcurl]").val();
            var username = $("[name=username]").val();
            var password = $("[name=password]").val();
            var driver = $("[name=driver]").val();

            var nextURL = "";
            if ($("#page").val() == "01") {
               nextURL = "sqlinput_ajaxprocessor.jsp";
            } else if ($("#page").val() == "02") {
               nextURL = "pickuielement_ajaxprocessor.jsp";
            } else if ($("#page").val() == "03") {
               nextURL = "preview_ajaxprocessor.jsp";
            }


           sendAjaxRequest(nextURL);

        });
    });
</script>

<!--link media="all" type="text/css" rel="stylesheet" href="css/registration.css"/-->
<fmt:bundle basename="org.wso2.carbon.bam.analyzer.ui.i18n.Resources">
    <carbon:breadcrumb label="main.analyzer"
                       resourceBundle="org.wso2.carbon.bam.analyzer.ui.i18n.Resources"
                       topPage="false" request="<%=request%>"/>

<%

    String jdbcurl = (session.getAttribute("jdbcurl") != null) ? ((String[]) session.getAttribute("jdbcurl")) [0] : "jdbc:h2:/Users/mackie/tmp/jaggery-1.0.0-SNAPSHOT_M4/repository/database/WSO2CARBON_DB;DB_CLOSE_ON_EXIT=FALSE";
    String driver = (session.getAttribute("driver") != null) ? ((String[]) session.getAttribute("driver")) [0] : "org.h2.Driver";
    String username = (session.getAttribute("username") != null) ? ((String[]) session.getAttribute("username")) [0] : "wso2carbon";
    String password = (session.getAttribute("password") != null) ? ((String[]) session.getAttribute("password")) [0] : "wso2carbon";

%>

    <div id="middle">
        <h2>Gadget Generator Wizard</h2>

        <div id="workArea">
            <h3 id="step-title">Step 1 of 5 : Enter Data Source</h3>


                <table class="styledLeft" id="userAdd" width="60%">
                    <thead>
                    <tr>
                        <th id="page-title">Enter Data Source</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td class="formRaw">
                            <table class="normal">
                                <tbody id="change-area">
                                <form>
                                    <tr>
                                        <td>JDBC URL<font color="red">*</font>
                                        </td>
                                        <td><input type="text" name="jdbcurl" value="<%=jdbcurl%>" style="width:150px"/></td>
                                    </tr>
                                    <tr>
                                        <td>Driver Class Name<font color="red">*</font></td>
                                        <td><input type="text" name="driver" value="<%=driver%>" style="width:150px"/></td>
                                    </tr>
                                    <tr>
                                        <td>User Name<font color="red">*</font></td>
                                        <td><input type="text" name="username" value="<%=username%>" style="width:150px"/></td>
                                    </tr>
                                    <tr>
                                        <td>Password<font color="red">*</font></td>
                                        <td><input type="password" name="password" value="<%=password%>" style="width:150px"></td>
                                    </tr>
                                    <tr>
                                        <input type="button" value="Validate Connection" id="validate"/>
                                    </tr>
                                    <input type="hidden" name="page" id="page" value="1">
                                </form>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td class="buttonRow">
                            <input type="button" id="back" value="Back">
                            <input type="button" id="next" value="Next">
                            <input type="button" id="generate" value="Generate">

                        </td>
                    </tr>
                    </tbody>
                </table>
        </div>


    </div>
</fmt:bundle>