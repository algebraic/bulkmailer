$(function () {

    // test data
    $(":text").each(function() {
        $(this).val($(this).attr("placeholder")).trigger("blur");
        $("#body").val("hi there");
    });

    // blank file on click so change sill fires
    $("#emaillist").click(function () {
        $("#result").text("");
        $("#emaillist").val("");
    });

    $("#emaillist").change(function (e) {
        var file = e.target.files[0];
        var formData = new FormData();
        formData.append('file', file);

        $.ajax({
            url: '/check',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (data) {
                console.log(data);
                $("#result").text(data + " email addresses loaded");
            }
        });

    });

    $("#submit").click(function () {
        var file = $("#emaillist")[0].files[0];
        var formData = new FormData();
        formData.append('file', file);
        formData.append('from', $("#from").val());
        formData.append('subject', $("#subject").val());
        formData.append('body', $("#body").summernote("code"));

        $.ajax({
            url: '/load',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function (data) {
                console.log(data);
            }
        });

    });

    // wysiwyg editor
    $("#body").summernote({
        placeholder: 'Message text',
        tabsize: 2,
        height: 400
    });

    // reset button
    $("#reset").click(function () {
        $("#result").text("");
        $("#body").summernote("reset")
    });
});