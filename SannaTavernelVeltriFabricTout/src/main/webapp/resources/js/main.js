function togglePasswordVisibility() {
    var passwordInput = document.getElementById("password");
    var eyeIcon = document.getElementById("eye");

    if (passwordInput.type === "password") {
        passwordInput.type = "text";
        eyeIcon.src = appContextPath + '/resources/images/prive.png';
    } else {
        passwordInput.type = "password";
        eyeIcon.src = appContextPath + '/resources/images/show.png';
    }
}
