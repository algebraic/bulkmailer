<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>

<script src="https://code.jquery.com/jquery-3.4.1.min.js"
    integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo=" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.bundle.min.js"
    integrity="sha384-xrRywqdh3PHs8keKZN+8zzc5TX0GRTLCcmivcbNJWm2rs5C8PRhcEn3czEjhAO9o"
    crossorigin="anonymous"></script>
<link href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet"
    integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"
    integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
    crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.bundle.min.js"
    integrity="sha384-xrRywqdh3PHs8keKZN+8zzc5TX0GRTLCcmivcbNJWm2rs5C8PRhcEn3czEjhAO9o"
    crossorigin="anonymous"></script>

<script src="js/material.float.labels.js"></script>
<link rel="stylesheet" href="css/material.float.labels.css">

<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/loading.io.css">

<link href="http://cdnjs.cloudflare.com/ajax/libs/summernote/0.8.12/summernote-bs4.css" rel="stylesheet">
<script src="http://cdnjs.cloudflare.com/ajax/libs/summernote/0.8.12/summernote-bs4.js"></script>
<script src="https://kit.fontawesome.com/208550a0ca.js"></script>

<script src="js/index.js"></script>


<head>
    <title>Bulk Mailer</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
</head>

<body>

    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <a class="navbar-brand" href="index.html">
            <img src="img/lara_small.png">
            <span class="ml-2">Bulk Mailer</span>
        </a>
    </nav>

    <div id="overlay" class="d-none">
        <div class="lds-grid" id="load-icon"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div>
        <div id="completed-msg" class="alert" role="alert">
            <h4 class="alert-heading">Process Completed</h4>
            <p>The email process has completed with <a id="download"></a></p>
            <hr>
            <p class="mb-0" id="fail-list"></p>
            <button id="close" class="btn">Close</button>
        </div>
    </div>
<br>
    <div class="page-content hide float-labels">
        <div class="container">
            <form>
                <div class="row">
                    <div class="col-lg-12">
                        <div class="card">
                            <div class="card-body">
                                <h6 class="my-4">Select csv file with email addresses:</h6>
                                <div class="form-group">
                                    <input type="file" name="emaillist" id="emaillist" class="form-control-file">
                                    <div class="lds-hourglass-sm d-none" id="loading"><div></div><div></div></div>
                                    <small id="file-note" class="text-info">emails addresses must be in a csv file with "email" as the header row</small>
                                    <small id="result" class="d-none"></small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <br>

                <div class="message-data">
                    <div class="row">
                        <div class="col-lg-6">
                            <div class="card">
                                <div class="card-body">
                                    <div class="form-group">
                                        <label for="from" class="control-label">From Address</label>
                                        <input type="text" class="form-control" id="from" name="from" placeholder="noreply@michigan.gov">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-6">
                            <div class="card">
                                <div class="card-body">
                                    <div class="form-group">
                                        <label for="subject" class="control-label">Message Subject</label>
                                        <input type="text" class="form-control" id="subject" name="subject" placeholder="Message Subject">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-lg-12">
                            <div class="form-group mt-4">
                                <textarea class="form-control" id="body" name="body"></textarea>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row my-5">
                    <div class="col-lg-12 text-center mb-1">
                        <button type="button" id="submit" class="btn btn-dark">Submit</button>
                        <button type="reset" id="reset" class="btn btn-dark">Reset</button>
                    </div>
                </div>
            </form>

            <div class="row fixed-bottom">
                <div class="col-lg-12 text-center">
                    <div class="alert-dark bg-dark my-0 p-0 text-light" id="footer">
                        <small style="top: 10px; position: relative;" id="footer-text">LARA Bulk Mailer</small>
                        <!-- emails addresses must be in a csv file with "email" as the header row -->
                        <div class="progress d-none" id="progress" style="height: 50px">
                            <div class="progress-bar progress-bar-striped progress-bar-animated bg-info" id="progressbar"role="progressbar"
                                aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>
    
</body>

</html>