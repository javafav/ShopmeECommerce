$(document).ready(function() {
    // Function to show the dropdown menu on hover
$(".navbar .dropdown").hover(
    function() {
        var $dropdownMenu = $(this).find('.dropdown-menu').first();
        setTimeout(function() {
            $dropdownMenu.stop(true, true).slideDown();
        }, 500); // Adjust delay time as needed
    },
    function() {
        var $dropdownMenu = $(this).find('.dropdown-menu').first();
        setTimeout(function() {
            $dropdownMenu.stop(true, true).slideUp();
        }, 350); // Adjust delay time as needed
    }
);
});