

function addNextDetailSection(){
	allDivDetails = $("[id^= 'divDetail']");
	divDetailCount = allDivDetails.length
	
	
	htmlDetailsSection = `
			<div class="form-inline" id="divDetail${divDetailCount}">
		    
		      <label class ="m-3">Name:</label>
		      <input type="text" maxlength="255"  class="form-control w-25" name="detailsNames">
		  
		     
		      <label class="m-3">Value:</label>
		      <input type="text" maxlength="255"  class="form-control w-25"  name="detailsValues">
		    
		
		
			
	</div> 
	
	`;
	
	$("#divProductDetails").append(htmlDetailsSection);
	
	previousDetailSection = allDivDetails.last();
	previousDetailId = previousDetailSection.attr("id");
	
	
		htmlLinkDetailRemove = `
  <a class="btn fa-regular fa-circle-xmark fa-2x icon-dark" 
         href="javascript:removeDetailSectionById('${previousDetailId}')" 
     title = "Remove this Detail"></a>
`;
	

previousDetailSection.append(htmlLinkDetailRemove);

	
	

	

}


function removeDetailSectionById(id){
	
	$("#"+id).remove();
}