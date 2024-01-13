console.log("this is script file")

const toggleSidebar = () => {


    if ($('.sidebar').is(":visible")) {
        //true
        //closed

        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");


    } else {
        //false
        //open sidebar
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }

}

const toggleSidebarNormal = () => {

    if ($collapse.hasClass("show")) {
        // Close the sidebar as a dropdown
        $collapse.removeClass("show");
    } else {
        // Open the sidebar as a dropdown
        $collapse.addClass("show");
    }
};


/* reset from after writing new story
document.getElementById('myForm').addEventListener('submit', function(e) {
	    	    e.preventDefault(); // Prevent the default form submission mechanism

	    	    const formData = new FormData(this); // This captures the form data

	    	    // Send the form data asynchronously
	    	    fetch(this.action, {
	    	        method: this.method,
	    	        body: formData
	    	    })
	    	    .then(response => {
	    	        if (response.ok) {
	    	            // Clear the form after successful submission
	    	            this.reset();
	    	        } else {
	    	            console.error('Form submission failed:', response.statusText);
	    	        }
	    	    })
	    	    .catch(error => {
	    	        console.error('There was an error when submitting the form:', error);
	    	    });
	    	});

*/
function checkPassword(event) {
	
	
	    let password = document.getElementById("pass1").value;
	    let confirmPassword = document.getElementById("pass2").value;
	    
	    console.log(password,confirmPassword);
	    
	    let message = document.getElementById("message");
	    
	    if(password.length !=0){
			if(password == confirmPassword){
				message.textContent = "Passwords match";
				message.style.backgroundColor = "#3ae374";
			}
			else{
				event.preventDefault(); 
				message.textContent = "Passwords do not match";
				message.style.backgroundColor = "#ff4d4d";
			}
			
		}
	    else{
			event.preventDefault(); 
			alert("Password can't be empty");
			message.textContent = "";
		}
	}


function validate(){
	var pass = document.getElementById("pass1");
	var upper = document.getElementById("upper");
	var lower = document.getElementById("lower");
	var num = document.getElementById("number");
	var len = document.getElementById("length");
	var sp_char = document.getElementById("special_char");
	
	//check if pass value contain number
	if(pass.value.match(/[0-9]/)){//match is function which matches a regular expressions
	//password contain 0 to 9 number then
		num.style.color = "green"
			
	}
	else{
		//otherwise
		num.style.color = "red"
	}
	
	//same way just copy and paste
	//check if pass value contain upper
	if(pass.value.match(/[A-Z]/)){//match is function which matches a regular expressions
	//password contain A to Z number then
		upper.style.color = "green"
			
	}
	else{
		//otherwise
		upper.style.color = "red"
	}
	
	//same way just copy and paste
	//check if pass value contain lower
	if(pass.value.match(/[a-z]/)){//match is function which matches a regular expressions
	//password contain a to z number then
		lower.style.color = "green"
			
	}
	else{
		//otherwise
		lower.style.color = "red"
	}
	
	
	//same way just copy and paste
	//checking for special symbols
	if(pass.value.match(/[!\@\#\$\%\^\&\*\(\)\_\-\+\=\?\>\<\.\,]/)){//match is function which matches a regular expressions
	//type all special characters which you want validate like ?>, but type all
	//after typing forward slash behind
		sp_char.style.color = "green"
	//it returns true if those characters are in password
			
	}
	else{
		//otherwise
		sp_char.style.color = "red"
	}
	
	//same way just copy and paste
	//checking length of password
	if(pass.value.length<6){//match is function which matches a regular expressions
	//if password is less than 6
		len.style.color = "red"
			
	}
	else{
		//otherwise
		len.style.color = "green"
	}

}

//now make new function to check confirm password
function confirm(){
	var pass1 = document.getElementById("pass1");
	var pass2 = document.getElementById("pass2");
	if(pass1.value == pass2.value)
	{
		document.getElementById("upper").style.display = "none";
		document.getElementById("lower").style.display = "none";
		document.getElementById("number").style.display = "none";
		document.getElementById("length").style.display = "none";
		document.getElementById("special_char").style.display = "none";
	}
	else{
		document.getElementById("upper").style.display = "block";
		document.getElementById("lower").style.display = "block";
		document.getElementById("number").style.display = "block";
		document.getElementById("length").style.display = "block";
		document.getElementById("special_char").style.display = "block";
	}
	
}





var staticBackdropModal = document.getElementById('staticBackdrop');  // Updated ID based on the modal's ID
var myInput = document.getElementById('myInput');

staticBackdropModal.addEventListener('shown.bs.modal', function () {
  myInput.focus();
});




