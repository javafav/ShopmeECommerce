function clearFilter() {
	window.location = moduleURL;
}

 function handleDetailLinkClick(cssClass, modalId) {
        $(cssClass).on("click", function(e) {
            e.preventDefault();
            var linkDetailURL = $(this).attr("href");
            $(modalId).modal("show").find(".modal-content").load(linkDetailURL);
        });
    }
    
    function handleDefaultDetailLinkClick() {
    	handleDetailLinkClick(".link-detail", "#detailModal");
    }
