<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FabricTout</title>

    <!-- slider stylesheet -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/owl.carousel.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/fonts.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/responsive.css" />
</head>
<body>
  <div class="hero_area">
    <header class="header_section">
        <div class="container">
            <nav class="navbar navbar-expand-lg custom_nav-container">            
                <a class="navbar-brand" href="${pageContext.request.contextPath}/Login"> 
                <img src="${pageContext.request.contextPath}/resources/images/logo.png" alt="">
                    <span>FabricTout</span>
                </a>
                <button class="navbar-toggler" type="button" 
                    data-toggle="collapse" data-target="#navbarSupportedContent" 
                    aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="s-1"></span>
                    <span class="s-2"></span>
                    <span class="s-3"></span>
                </button>
            </nav>
        </div>
    </header>

    <!-- slider section -->
    <section class="slider_section">
        <div class="container">
            <div class="row">
                <div class="col-md-6">
                <%@ include file="../ErrorsHeader.jsp" %>
				    <div class="rounded-form-container">
				        <h1 class="text-center mb-4">Login</h1>
				        <div class="card-body">
				            <form action="Login" method="post" class="needs-validation" novalidate>
				                <div class="mb-3">
				                    <label for="registrationCode" class="form-label">Registration Code:</label>
				                    <input type="text" id="registrationCode" name="registrationCode" class="form-control" required>
				                    <div class="invalid-feedback">Please enter your registration code.</div>
				                </div>
				                <div class="mb-3">
				                    <label for="password" class="form-label">Password:</label>
				                    <div class="input-group">
				                        <input type="password" id="password" name="password" class="form-control" required>
				                        <span class="input-group-text">
				                            <img id="eye" class="eye-icon" src="${pageContext.request.contextPath}/resources/images/show.png" 
				                            alt="Show/Hide Password" onclick="togglePasswordVisibility()" style="cursor: pointer;">
				                            
				                        </span>
				                    </div>
				                    <div class="invalid-feedback">Please enter your password.</div>
				                </div>
				                <div class="d-grid">
				                    <button type="submit" class="btn btn-primary">Log In</button>
				                </div>
				            </form>
				        </div>
				    </div>
				</div>

                <div class="col-lg-5 col-md-6 offset-lg-1">
                    <div class="img_content">
                        <div class="img_container">
                            <div id="carouselExampleControls" class="carousel slide" data-ride="carousel">
                                <div class="carousel-inner">
                                    <div class="carousel-item active">
                                        <div class="img-box">
                                            <img src="${pageContext.request.contextPath}/resources/images/image1.jpg" alt="Slide Image">
                                        </div>
                                    </div>
                                    <div class="carousel-item">
                                        <div class="img-box">
                                            <img src="${pageContext.request.contextPath}/resources/images/image2.jpg" alt="Slide Image">
                                        </div>
                                    </div>
                                    <div class="carousel-item">
                                        <div class="img-box">
                                            <img src="${pageContext.request.contextPath}/resources/images/image3.jpg" alt="Slide Image">
                                        </div>
                                    </div>
                                </div>
                            </div>                          
                            <a class="carousel-control-prev" href="#carouselExampleControls" role="button" data-slide="prev">
			                <span class="sr-only">Previous</span>
			              </a>
			              <a class="carousel-control-next" href="#carouselExampleControls" role="button" data-slide="next">
			                <span class="sr-only">Next</span>
			              </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <!-- end slider section -->
  </div>       
   <div class="text-center mt-3">
       <small class="text-muted">&copy; 2024 FabricTout</small>
   </div>


  <script src="${pageContext.request.contextPath}/resources/js/jquery-3.4.1.min.js"></script>
  <script src="${pageContext.request.contextPath}/resources/js/bootstrap.js"></script>
  <script src="${pageContext.request.contextPath}/resources/js/main.js"></script>
 <script>
    var appContextPath = '${pageContext.request.contextPath}';
</script>
 
</body>
</html>
