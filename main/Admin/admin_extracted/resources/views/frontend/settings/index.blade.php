@extends('layout.master')

@section('content')

    @php
        // Retrieve the Ads model
        $ads = \App\Models\Ads::find(1);
    @endphp

    <div class="content-wrapper">
        <!-- Content -->
        @include('message')

        <div class="container-xxl flex-grow-1 container-p-y">
            <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light"><a
                        href="{{ route('dashboard') }}">Dashboard</a>/</span> Settings</h4>

            @if ($errors->any())
                @foreach ($errors->all() as $error)
                    <div class="alert alert-danger">{{ $error }}</div>
                @endforeach
            @endif

            <!-- Bootstrap Alert (initially hidden) -->
            <div id="myAlert" class="alert alert-success alert-dismissible" role="alert" style="display: none;">
                Base URL copied successfully.
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>


            <div class="row">
                <div class="col-md-12">
                    <ul class="nav nav-pills flex-column flex-md-row mb-3">


                        <li class="nav-item">
                            <a class="tablinks nav-link " id="defaultOpen" onclick="openTab(event, 'About')">
                                <i class="bx bxs-user me-1"></i>About</a>
                        </li>

                        <li class="nav-item">
                            <a class="tablinks nav-link" onclick="openTab(event, 'App Settings')">
                                <i class="bx bxs-cog me-1"></i>App Settings</a>
                        </li>

                        <li class="nav-item">
                            <a class="tablinks nav-link " onclick="openTab(event, 'Privacy Policy')">
                                <i class="bx bx-clipboard me-1"></i>Privacy Policy</a>
                        </li>


                        <li class="nav-item">
                            <a class="tablinks nav-link" onclick="openTab(event, 'Ads Settings')">
                                <i class="bx bxs-video me-1"></i>Ads Settings</a>
                        </li>

                    </ul>



                    <div id="About" class="tabcontent card mb-4">
                        <div class="card-body">
                            <div class="row">
                                <form action="{{ route('settings.about') }}" method="POST" enctype="multipart/form-data">
                                    @csrf

                                    <div class="form-group row mb-4">
                                        <label class="col-sm-3 col-form-label">Email</label>
                                        <div class="col-sm-9">
                                            <input type="text" name="app_email" id="app_email"
                                                value="{{ $setting->app_email }}" class="form-control">
                                        </div>
                                    </div>
                                    <div class="form-group row mb-4">
                                        <label class="col-sm-3 col-form-label">Author</label>
                                        <div class="col-sm-9">
                                            <input type="text" name="app_author" id="app_author"
                                                value="{{ $setting->app_author }}" class="form-control">
                                        </div>
                                    </div>
                                    <div class="form-group row mb-4">
                                        <label class="col-sm-3 col-form-label">Contact</label>
                                        <div class="col-sm-9">
                                            <input type="text" name="app_contact" id="app_contact"
                                                value="{{ $setting->app_contact }}" class="form-control">
                                        </div>
                                    </div>

                                    <div class="form-group row mb-4">
                                        <label class="col-sm-3 col-form-label">Website</label>
                                        <div class="col-sm-9">
                                            <input type="text" name="app_website" id="app_website"
                                                value="{{ $setting->app_website }}" class="form-control">
                                        </div>
                                    </div>
                                    <div class="form-group row mb-4">
                                        <label class="col-sm-3 col-form-label">Developed By</label>
                                        <div class="col-sm-9">
                                            <input type="text" name="app_developed_by" id="app_developed_by"
                                                value="{{ $setting->app_developed_by }}" class="form-control">
                                        </div>
                                    </div>
                                    <div class="form-group row mb-4">
                                        <label class="col-sm-3 col-form-label">App Description</label>
                                        <div class="col-sm-9">
                                            <textarea name="app_description" id="app_description" class="form-control">{{ $setting->app_description }}</textarea>
                                        </div>
                                    </div>
                                    <div class="form-group row mb-4">
                                        <div class="col-sm-3"></div>
                                        <div class="col-sm-9">
                                            <button type="submit" name="about_submit" class="btn btn-primary"
                                                style="min-width: 100px;">Save</button>
                                        </div>
                                    </div>

                                </form>
                            </div>
                        </div>
                    </div>


                    <div id="App Settings" class="tabcontent card mb-4">
                        <div class="card-body">
                            <div class="container">

                                <form action="{{ route('settings.post') }}" method="POST" enctype="multipart/form-data">
                                    @csrf

                                    <div class="row">
                                        <div class="form-group col row mb-4">
                                            <label class="col-sm-4 col-form-label">App Maintenance</label>
                                            <div class="col-sm-8">
                                                <label class="switch">
                                                    <input type="checkbox" id="isMaintenance" name="isMaintenance"
                                                        class="cbx hidden"
                                                        {{ $setting->isMaintenance == 1 ? 'checked' : '' }} />
                                                    <span class="slider round"></span>
                                                </label>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="mb-3">
                                            <label for="url" class="form-label">More Apps url</label>
                                            <input type="text" required name="more_apps_url" class="form-control"
                                                id="more_apps_url" value="{{ $setting->more_apps_url }}"
                                                placeholder="https://play.google.com/store/apps/" />
                                        </div>
                                    </div>


                                    <div class="row">
                                        <div class="mb-3">
                                            <label for="url" class="form-label">App Base url</label>
                                            <input type="text" required name="app_base_url" class="form-control"
                                                id="base_url" value="{{ url('/') }}" readonly
                                                data-bs-toggle="tooltip" data-bs-offset="0,4" data-bs-placement="left"
                                                data-bs-html="true"
                                                title="<i class='bx bx bxs-copy' ></i> <span>Click here to copy the base URL</span>" />
                                        </div>
                                    </div>


                                    <div class="row align-items-start">

                                        <div class="col mb-4">

                                            <button type="submit" class="btn btn-primary"
                                                style="min-width: 100px;">Save</button>

                                        </div>

                                    </div>


                                </form>
                            </div>

                        </div>

                    </div>




                    <div id="Privacy Policy" class="tabcontent card mb-4">
                        <div class="card-body">
                            <form action="{{ route('settings.privacy') }}" method="POST" enctype="multipart/form-data">
                                @csrf

                                <div class="form-group">
                                    <div class="form-line">

                                        <textarea class="form-control" name="privacy_policy" id="privacy_policy" class="form-control" cols="60"
                                            rows="10">{{ $setting->privacy_policy }}</textarea>

                                        <script>
                                            CKEDITOR.replace('privacy_policy');
                                            CKEDITOR.config.height = 300;
                                        </script>


                                    </div>
                                </div>
                                <button type="submit" class="btn btn-primary mt-4">Save</button>
                            </form>

                        </div>

                    </div>



                    <form method="post" action="{{ route('ads') }}" enctype="multipart/form-data">@csrf
                        <div id="Ads Settings" class="tabcontent card mb-4">
                            <script>
                                window.localStorage.setItem('AD_STATUS', {{ $ads->ad_status }})
                            </script>
                            <div class="card-body">

                                <div class="body">
                                    <div class="mb-3">
                                        <label for="type" class="form-label">Ad Status</label>
                                        <select required class="form-select" name="ad_status" id="ad_status"
                                            aria-label="Default select example">
                                            <option {{ $ads->ad_status == '1' ? 'selected' : '' }} value="1">On
                                            </option>
                                            <option {{ $ads->ad_status == '0' ? 'selected' : '' }} value="0">Off
                                            </option>
                                        </select>
                                    </div>

                                    <div id="ads_content" class="card">
                                        <div class="card-body" style="background: rgb(237, 232, 255)">
                                            <div id="ad_status_on">
                                                <div class="mb-3">
                                                    <label for="type" style="color: rgb(74, 0, 0)"
                                                        class="form-label">Primary Ad Network</label>
                                                    <select required class="form-select" name="main_ads" id="main_ads"
                                                        aria-label="Default select example">

                                                        <option {{ $ads->main_ads == 'admob' ? 'selected' : '' }}
                                                            value="admob">
                                                            AdMob</option>
                                                        <option {{ $ads->main_ads == 'unity' ? 'selected' : '' }}
                                                            value="unity">
                                                            Unity Ads</option>

                                                    </select>
                                                </div>

                                                <div style="display: {{ $ads->main_ads == 'unity' ? 'none' : '' }}"
                                                    id="admob_ads">

                                                    <div class="form-group">
                                                        <div class="">
                                                            <div class="form-line">
                                                                <div class="font-12">AdMob App ID</div>
                                                                <div class="ex2" style="margin-top: 0px;">Important :
                                                                    Your
                                                                    <b>AdMob App ID</b> must be added programmatically
                                                                    inside
                                                                    Android Studio Project in the <b>res/values/ads.xml</b>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <br>

                                                    <div class="mb-3">
                                                        <label for="admob_banner_unit_id" class="form-label">AdMob Banner
                                                            Ad
                                                            Unit ID</label>
                                                        <input type="text" required name="admob_banner_unit_id"
                                                            class="form-control" value="{{ $ads->admob_banner_unit_id }}"
                                                            id="admob_banner_unit_id" />
                                                    </div>


                                                    <div class="mb-3">
                                                        <label for="admob_interstitial_unit_id" class="form-label">AdMob
                                                            Interstitial Ad Unit ID</label>
                                                        <input type="text" required name="admob_interstitial_unit_id"
                                                            class="form-control"
                                                            value="{{ $ads->admob_interstitial_unit_id }}"
                                                            id="admob_interstitial_unit_id" />
                                                    </div>


                                                    <div class="mb-3">
                                                        <label for="admob_native_unit_id" class="form-label">AdMob Native
                                                            Ad
                                                            Unit ID</label>
                                                        <input type="text" required name="admob_native_unit_id"
                                                            class="form-control" value="{{ $ads->admob_native_unit_id }}"
                                                            id="admob_native_unit_id" />
                                                    </div>


                                                    <div class="mb-3">
                                                        <label for="admob_app_open_unit_id" class="form-label">AdMob App
                                                            Open
                                                            Ad Unit ID</label>
                                                        <input type="text" required name="admob_app_open_unit_id"
                                                            class="form-control"
                                                            value="{{ $ads->admob_app_open_unit_id }}"
                                                            id="admob_app_open_unit_id" />
                                                    </div>
                                                </div>

                                                <div style="display: {{ $ads->main_ads == 'admob' ? 'none' : '' }}"
                                                    id="unity_ads">



                                                    <div class="mb-3">
                                                        <label for="admob_app_open_unit_id" class="form-label">Unity Game
                                                            ID</label>
                                                        <input type="text" required name="unity_game_id"
                                                            class="form-control" value="{{ $ads->unity_game_id }}"
                                                            id="unity_game_id" />
                                                    </div>


                                                    <div class="mb-3">
                                                        <label for="admob_app_open_unit_id" class="form-label">Unity
                                                            Banner Ad Placement ID</label>
                                                        <input type="text" required name="unity_banner_placement_id"
                                                            class="form-control"
                                                            value="{{ $ads->unity_banner_placement_id }}"
                                                            id="unity_banner_placement_id" />
                                                    </div>

                                                    <div class="mb-3">
                                                        <label for="admob_app_open_unit_id" class="form-label">Unity
                                                            Interstitial Ad Placement ID</label>
                                                        <input type="text" required
                                                            name="unity_interstitial_placement_id" class="form-control"
                                                            value="{{ $ads->unity_interstitial_placement_id }}"
                                                            id="unity_interstitial_placement_id" />
                                                    </div>
                                                </div>

                                            </div>

                                        </div>
                                    </div>

                                    <button type="submit" class="btn btn-primary mt-4">Insert</button>

                                </div>

                            </div>
                        </div>
                    </form>




                </div>
            </div>
        </div>
        <!-- / Content -->


        <div class="content-backdrop fade"></div>
    </div>



@endsection


@push('js')
    <script>
        $(document).ready(function() {
            // Add click event listener to the base URL input field
            $('#base_url').click(function() {
                // Show the alert when the input field is clicked
                $('#myAlert').show();
            });

            // Optional: Submit the form when the input field is clicked
            $('#base_url').click(function() {
                // Submit the form
                $('#submitButton').click();
            });
        });



        function copyBaseUrlOnClick() {
            // Get the input field
            var baseUrlInput = document.getElementById('base_url');

            // Select the text in the input field
            baseUrlInput.select();
            baseUrlInput.setSelectionRange(0, 99999); // For mobile devices

            // Copy the text inside the input field
            document.execCommand('copy');

            // Deselect the input field
            baseUrlInput.setSelectionRange(0, 0);
        }

        // Add event listener to the input field to trigger copy when it is clicked
        document.getElementById('base_url').addEventListener('click', copyBaseUrlOnClick);





        $(document).ready(function(e) {

            $("#main_ads").change(function() {
                var type = $("#main_ads").val();

                if (type == "admob") {
                    $("#admob_ads").show();
                    $("#unity_ads").hide();
                }

                if (type == "unity") {
                    $("#admob_ads").hide();
                    $("#unity_ads").show();
                }

            });

            $("#ad_status").change(function() {
                var sts = $("#ad_status").val();

                if (sts == 1) {
                    $("#ads_content").show();
                } else {
                    $("#ads_content").hide();
                }

            });
        });

        var ad_status = window.localStorage.getItem('AD_STATUS');
        if (ad_status == 1) {
            $("#ads_content").show();
        } else {
            $("#ads_content").hide();
        }



        function openTab(evt, tabName) {
            var i, tabcontent, tablinks;
            tabcontent = document.getElementsByClassName("tabcontent");
            for (i = 0; i < tabcontent.length; i++) {
                tabcontent[i].style.display = "none";
            }
            tablinks = document.getElementsByClassName("tablinks");
            for (i = 0; i < tablinks.length; i++) {
                tablinks[i].className = tablinks[i].className.replace(" active", "");
            }
            document.getElementById(tabName).style.display = "block";
            evt.currentTarget.className += " active";
            window.localStorage.setItem('TAB_NAME_SETTING', tabName)
        }

        const previousTab = window.localStorage.getItem('TAB_NAME_SETTING');
        //console.log(previousTab);
        if (previousTab) {
            const xpath = `//a[text()='${previousTab}']`;
            const elem = document.evaluate(xpath, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null)
                .singleNodeValue;
            elem.click();
        } else {
            document.getElementById("defaultOpen").click();
        }
    </script>
@endpush
