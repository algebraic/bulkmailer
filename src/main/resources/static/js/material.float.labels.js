$(function () {
    // float-label focus toggling
    $(".form-control").on("focus blur", function (e) {
        $(this).parents(".form-group").toggleClass("focused", (e.type === "focus" || this.value.length > 0));
    }).trigger("blur");
    // float-label reset behavior
    $("form").bind("reset", function () {
        $(".form-group.focused").removeClass("focused");
    });

    // prevent fouc
    $(".page-content.hide").removeClass("hide");
});
