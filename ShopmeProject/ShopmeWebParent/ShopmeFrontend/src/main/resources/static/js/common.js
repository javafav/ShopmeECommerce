  function customizeDropDownMenu() {
        $(".navbar .dropdown").hover(
            function() {
                $(this).find('.dropdown-menu').first().stop(true, true).delay(250).slideDown();
            },
            function() {
                $(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();
            }
        );

        $(".dropdown > .nav-link").on("click", function(e) {
            e.preventDefault(); // Prevent default link behavior
            location.href = this.href; // Redirect to the clicked link
        });
    }