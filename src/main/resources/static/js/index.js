$(function() {

    // test data
    // $(":text").each(function() {
    //     $(this).val($(this).attr("placeholder")).trigger("blur");
    //     $("#body").val("testing email system");
    // });

    // blank file on click so change sill fires
    $("#emaillist").click(function() {
        $("#result").text("");
        $("#emaillist").val("");
    });

    $("#emaillist").change(function(e) {
        $("#file-note, #loading").toggleClass("d-none");
        var file = e.target.files[0];
        var formData = new FormData();
        formData.append('file', file);

        $.ajax({
            url: '/check',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(data) {
                $("#loading").toggleClass("d-none");
                $("#result").text(data + " email addresses loaded").toggleClass("d-none");
            }
        });

    });

    $("#from").keydown(function(e) {
        // on tab, use default from if nothing entered
        if (e.which == 9 && $(this).val() == "") {
            $(this).val("noreply@michigan.gov");
        }
    });

    $("#submit").click(function() {
        $(".error").removeClass("error");
        var error = false;

        $(":text.form-control, :file.form-control-file").each(function() {
            var $this = $(this);
            if ($this.val() == "" && $this.attr("id")) {
                error = true;
                $this.parents(".card").addClass("error");
            }
            if ($this.attr("id") == "from") {
                var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
                var validEmail = re.test(String($this.val()).toLowerCase());
                if (!validEmail) {
                    error = true;
                    $this.parents(".card").addClass("error");
                }
            }
        });

        var $note = $(".note-editable");
        if ($note.text() == "") {
            $note_parent = $note.parents(".note-editor");
            $note_parent.addClass("error");
            error = true;
        }

        if (!error) {
            $("#footer-text, #progress, #overlay").toggleClass("d-none");
            var file = $("#emaillist")[0].files[0];
            var formData = new FormData();
            formData.append('file', file);
            formData.append('from', $("#from").val());
            formData.append('subject', $("#subject").val());
            formData.append('body', $("#body").summernote("code"));

            getProgress();

            $.ajax({
                url: '/load',
                type: 'POST',
                async: true,
                data: formData,
                processData: false,
                contentType: false,
                success: function(data) {
                    console.log(data);
                }
            });
        }

    });

    // wysiwyg editor
    $("#body").summernote({
        placeholder: 'Message body',
        tabsize: 2,
        height: 300
    });

    // reset button
    $("#reset").click(function() {
        $("#result").text("").addClass("d-none");
        $("#file-note").removeClass("d-none");
        $("#body").summernote("reset");
        $(".error").removeClass("error");
    });

    $("#close").click(function() {
        // alert close button
        window.location.reload();
    });
});

function getProgress() {
    var progress = 0;
    $.ajax({
        url: '/getProgress',
        type: 'GET',
        async: false,
        success: function(data) {
            progress = data;
            $("#progressbar").text(progress + "%").attr("aria-valuenow", progress).css("width", progress + "%");
        },
        error: function() {
            console.error("error getting progress");
        }
    });
    if (progress < 100) {
        setTimeout(function() {
            getProgress();
        }, 1000);
    } else {
        $("#progressbar").text("100%").attr("aria-valuenow", 100).css("width", "100%");
        setTimeout(function() {
            // get any failed addresses
            $.ajax({
                url: '/getFails',
                type: 'GET',
                success: function(data) {
                    console.info(data);

                    var length = data.length;
                    var length_msg;
                    if (length == 1) {
                        length_msg = "1 error";
                    } else {
                        length_msg = length + " errors";
                    }
                    $("#download").text(length_msg);

                    var classname = "success";
                    if (data.length > 0) {
                        classname = "warning";

                        // download list of failed addresses
                        var msg = "email\n";
                        $.each(data, function(index, value) {
                            msg += value + "\n";
                        });
                        var filename = 'export.csv';
                        var csv = 'data:text/csv;charset=utf-8,' + msg;
                        var uri = encodeURI(csv);
                        var $link = $("#download");
                        $link.attr("href", uri);
                        $link.attr("download", filename);
                    }
                    $("#completed-msg").addClass("alert-" + classname);
                    $("#close").addClass("btn-" + classname);
                    $("#footer-text, #progress").toggleClass("d-none");
                    $("#progressbar").text("0%").attr("aria-valuenow", 0).css("width", "0%");
                    $("#load-icon").hide();
                },
                error: function() {
                    console.error("error getting failed addresses");
                }
            });

        }, 3000);
    }
}