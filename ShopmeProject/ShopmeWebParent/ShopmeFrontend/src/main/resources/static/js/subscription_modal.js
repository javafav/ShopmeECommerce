
    $(document).ready(function () {
    
    	 $('#subscribeButton').click(function() {
    	        var email = $('#emailInput').val().trim();

    	        if (email === '') {
    	           showWarningModal('Please enter your email.');
    	            return;
    	        }

    
    	        $('#myModal').modal('show');
    	        $('#emailInput').val('');
    	  
    	        $.ajax({
    	            url: contextPath + 'subscribe/newsletter',
    	            type: 'GET',
    	            data: { emailForSubscription: email },
    	            success: function(response) {
    	                console.log(response);
    	            },
    	            error: function(xhr, status, error) {
    	                console.error('An error occurred: ' + error);
    	              
    	              $('#emailInput').val('');
    	                $('#myModal').modal('hide');
    	                showErrorModal('Failed to send subscription email. Please try again later.');
    	              
    	              
    	            }
    	        });
    	    });
    });