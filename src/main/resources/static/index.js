$(document).ready(function () {

    let searchParams = new URLSearchParams(window.location.search)
    Cookies.set("USER-CODE", searchParams.get("userCode"))

    $.ajaxSetup({
        beforeSend : function(xhr, settings) {
            if (settings.type == 'POST' || settings.type == 'PUT'
                || settings.type == 'DELETE') {
                if (!(/^http:.*/.test(settings.url) || /^https:.*/
                    .test(settings.url))) {
                    // Only send the token to relative URLs i.e. locally.
                    xhr.setRequestHeader("X-XSRF-TOKEN", Cookies.get('XSRF-TOKEN'));
                }
            }
        }
    });

    $("#logoutButton").click(function() {
        $.post("/logout", function () {
            $("#user").html('');
        })
    })

    $("#emailButton").click(function() {
        $.get("/api/email", function (data) {
            $("#emails").html(JSON.stringify(data));
        })
    })

})