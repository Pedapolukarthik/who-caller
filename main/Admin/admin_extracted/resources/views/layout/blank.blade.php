<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>@yield('title')</title>

    <link rel="shortcut icon" href="{{ asset('public/assets/installation/assets/img/favicon.ico') }}">

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="{{ asset('public/assets/installation/assets/css/bootstrap.min.css') }}">

    <link rel="stylesheet" href="{{ asset('public/assets/installation/assets/css/style.css') }}">

    <link rel="stylesheet" href="{{ asset('public/assets/installation/assets/css/custom.css') }}">




    <link rel="stylesheet" href="{{ asset('public/assets/vendor/css/theme-default.css') }}"
        class="template-customizer-theme-css" />

    <link rel="stylesheet" href="{{ asset('public/assets/css/demo.css') }}" />

    <link rel="stylesheet" href="{{ asset('public/assets/vendor/css/new.css') }}" />

    <style>
        .main-background-image {
            background-color: #F5F5F9;
        }
    </style>

</head>

<body>
    <section class="w-100 min-vh-100 bg-img position-relative py-5 main-background-image">



        <div class="custom-container">


            <div class="text-center text-primary mb-4">
                <h2>Whocaller AdminPanel Installation</h2>
                <h6 style="color: #2f2f49" class="fw-normal">Please proceed step by step with proper data according to
                    instructions'</h6>
            </div>

            @yield('content')


            <footer class="footer py-3 mt-4">
                <div class="content has-text-centered">

                    <p class="copyright-text mb-0">© {{ date('Y') }} | AndroPlaza, All rights reserved</p>
                </div>
            </footer>
        </div>
    </section>

    <script type="text/javascript">
        "use strict";

        $(".showLoder").on('click', function() {
            $('#loading').fadeIn();
        })
    </script>

</body>

<script src="{{ asset('public/assets/installation/assets/js/bootstrap.bundle.min.js') }}"></script>
<script src="{{ asset('public/assets/installation/assets/js/script.js') }}"></script>


</html>
