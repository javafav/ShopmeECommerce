$(document).ready(function() {
	
		$("a[name='linkRemoveDetail']").each(function(index) {
		    $(this).click(function() {
			removeDetailSectionByIndex(index)
		});
	});
	
	
});



function addNextDetailSection(){
	allDivDetails = $("[id^= 'divDetail']");
	divDetailCount = allDivDetails.length
	
	
	htmlDetailsSection = `
			<div class="form-inline" id="divDetail${divDetailCount}">
		      <input type="hidden" name=detailIDs value="0">
		      <label class ="m-3">Name:</label>
		      <input type="text" maxlength="255"  class="form-control w-25" name="detailNames">
		  
		     
		      <label class="m-3">Value:</label>
		      <input type="text" maxlength="255"  class="form-control w-25"  name="detailValues">
		    
		
		
			
	</div> 
	
	`;
	
	$("#divProductDetails").append(htmlDetailsSection);
	
	previousDetailSection = allDivDetails.last();
	previousDetailId = previousDetailSection.attr("id"); 
	
	
		htmlLinkDetailRemove = `
  <a class="btn fa-regular fa-circle-xmark fa-2x icon-silver" 
         href="javascript:removeDetailSectionById('${previousDetailId}')" 
     title = "Remove this detail"></a>
`;
	

previousDetailSection.append(htmlLinkDetailRemove);

	
	$("input[name='detailNames']").last().focus();
	
	

	

}

function removeDetailSectionById(id){
	
	$("#"+id).remove();
}

function removeDetailSectionByIndex(index){
	$("#divDetail" + index).remove();
	}



