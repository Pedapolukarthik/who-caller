@extends('layout.master')
@php
    use Carbon\Carbon;
@endphp

@section('content')
    <div class="content-wrapper">
        <!-- Content -->
        @include('message')


        <div class="container-xxl flex-grow-1 container-p-y">

            <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light"><a
                        href="{{ route('dashboard') }}">Dashboard</a>/</span>Notifications</h4>


            @if ($errors->any())
                @foreach ($errors->all() as $error)
                    <div class="alert alert-danger">{{ $error }}</div>
                @endforeach
            @endif

            <div class="row">
                <div class="col-md-12">
                    <ul class="nav nav-pills flex-column flex-md-row mb-3">

                        <li class="nav-item">
                            <a class="tablinks nav-link " id="defaultOpen" onclick="openTab(event, 'Notification Setting')">
                                <i class="bx bxs-cog me-1"></i>Notification Setting</a>

                        </li>
                        <li class="nav-item">
                            <a class="tablinks nav-link" onclick="openTab(event, 'Send Notification')">
                                <i class="bx bxs-paper-plane me-1"></i>Send Notification</a>

                        </li>

                        <li class="nav-item">
                            <a class="tablinks nav-link" onclick="openTab(event, 'Notification History')">
                                <i class="bx bx-time me-1"></i>Notification History</a>

                        </li>

                    </ul>


                    <form action="{{ route('notification.post') }}" method="POST">@csrf

                        <div id="Notification Setting" class="tabcontent card mb-4">
                            <h5 class="card-header">OneSignal Details</h5>

                            <div class="card-body">

                                <div class="mb-3">
                                    <label for="app_id" class="form-label">OneSignal App ID</label>
                                    <input type="app_id" required name="app_id" class="form-control" id="app_id"
                                        value="{{ $setting->onesignal_app_id }}" />
                                </div>

                                <div class="mb-3">
                                    <label for="rest_key" class="form-label">OneSignal Rest Key</label>
                                    <input type="rest_key" required name="rest_key" class="form-control" id="rest_key"
                                        value="{{ $setting->onesignal_rest_key }}" />
                                </div>

                                <button type="submit" class="btn btn-primary">Save</button>

                            </div>

                        </div>
                    </form>


                    <form action="{{ route('notification.send') }}" enctype="multipart/form-data" method="POST">@csrf

                        <div id="Send Notification" class="tabcontent card mb-4">
                            <h5 class="card-header">Send Notification</h5>

                            <div class="card-body">

                                <div class="mb-3">
                                    <label for="title" class="form-label">Title</label>
                                    <input type="text" required name="title" class="form-control" id="title"
                                        placeholder="hellow" />
                                </div>

                                <div class="mb-3">
                                    <label for="message" class="form-label">Message</label>
                                    <textarea class="form-control" name="message" id="message" rows="3"></textarea>
                                </div>

                                <div class="mb-3">
                                    <label for="image" class="form-label">Select Image</label>
                                    <input class="dropify form-control" name="image" type="file" id="image" />
                                </div>

                                <div class="mb-3">
                                    <label for="link" class="form-label">Url</label>
                                    <input type="url" name="link" class="form-control" id="link"
                                        placeholder="http://127.0.0.1:8000/notification" />
                                </div>

                                <button type="submit" class="btn btn-primary">Send</button>

                            </div>

                        </div>
                    </form>

                    <div id="Notification History" class="tabcontent card mb-4">


                        <div class="card">
                            <div class="table-responsive text-nowrap">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>#</th>
                                            <th>Image</th>
                                            <th>Title</th>
                                            <th>Time</th>
                                            <th>Send</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    @foreach ($notifications as $key => $notification)
                                        <tbody class="table-border-bottom-0">
                                            <tr>
                                                <td><i class="fab fa-angular fa-lg text-danger me-3"></i>
                                                    <strong>{{ ++$key }}</strong>
                                                </td>


                                                <td><img src="{{ asset('public/' . str_replace('public/', 'storage/', $notification->image)) }}"
                                                        width="50px" height="50px" alt="image" class="card-img" />
                                                </td>



                                                <td>{{ $notification->title }}</td>

                                                <td>{{ Carbon::parse($notification->created_at)->diffForHumans() }}</td>

                                                <td>

                                                    <button type="button" class="btn-sm rounded-pill round btn-primary"
                                                        data-toggle="modal"
                                                        data-target="#exampleModal{{ $notification->id }}">
                                                        Resend
                                                    </button>

                                                </td>
                                                <td>
                                                    <form id="delete_form"
                                                        action="{{ route('notification.delete', $notification->id) }}"method="POST">
                                                        @csrf
                                                        @method('DELETE')
                                                        <div class="d-flex">

                                                            <button
                                                                style=" border: none;
                                                            outline: none;
                                                            padding: 5px;
                                                            border-radius: 5px;"
                                                                class="btn-my text-danger bg-label-danger ">
                                                                <i class="bx bxs-trash text-danger"></i> DELETE
                                                            </button>
                                                        </div>
                                                    </form>
                                                </td>

                                            </tr>
                                            <!-- Modal -->
                                            <form action="{{ route('notification.resend') }}"
                                                enctype="multipart/form-data" method="POST">@csrf

                                                <div class="modal fade" id="exampleModal{{ $notification->id }}"
                                                    tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel"
                                                    aria-hidden="true" enctype="multipart/form-data"
                                                    data-notification-route="{{ route('notification.send') }}"
                                                    data-csrf-token="{{ csrf_token() }}">

                                                    <div class="modal-dialog modal-lg" role="document">
                                                        <!-- Add 'modal-lg' class for a larger modal -->
                                                        <div class="modal-content">
                                                            <div class="modal-header">
                                                                <h5 class="modal-title" id="exampleModalLabel">Resend
                                                                    Notification
                                                                </h5>
                                                                <button type="button" class="btn-close"
                                                                    data-dismiss="modal" aria-label="Close">
                                                                    <span aria-hidden="true">&times;</span>
                                                                </button>
                                                            </div>
                                                            <div class="modal-body">
                                                                <div class="mb-3">
                                                                    <label for="title" class="form-label">Title</label>
                                                                    <input type="text"
                                                                        value="{{ $notification->title }}" required
                                                                        name="title" class="form-control"
                                                                        id="title" placeholder="Hello" />
                                                                </div>

                                                                <div class="mb-3">
                                                                    <label for="message"
                                                                        class="form-label">Message</label>
                                                                    <textarea class="form-control" name="message" id="message" rows="3">{{ $notification->message }}</textarea>
                                                                </div>



                                                                <div class="mb-3">
                                                                    <label for="file" class="form-label">Image</label>
                                                                    <input
                                                                        data-default-file="{{ asset('public/' . str_replace('public/', 'storage/', $notification->image)) }}"
                                                                        class="dropify form-control" name="image"
                                                                        type="file" id="image" />
                                                                    <input type="hidden" name="current_image"
                                                                        value="{{ $notification->image }}" />
                                                                </div>



                                                                <div class="mb-3">
                                                                    <label for="link" class="form-label">Url</label>
                                                                    <input type="url"
                                                                        value="{{ $notification->url }}" name="link"
                                                                        class="form-control" id="link"
                                                                        placeholder="http://127.0.0.1:8000/notification" />
                                                                </div>
                                                            </div>
                                                            <div class="modal-footer">
                                                                <button type="button" class="btn btn-secondary"
                                                                    data-dismiss="modal">Close</button>
                                                                <button id="sendNotificationBtn" type="submit"
                                                                    class="btn btn-primary send-notification-btn"
                                                                    data-bs-target="#exampleModal{{ $notification->id }}">Send
                                                                    Notification</button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                            </form>

                                        </tbody>
                                    @endforeach

                                </table>
                            </div>
                        </div>

                    </div>


                </div>
            </div>
        </div>

    </div>
@endsection



@push('js')
    <script>
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
            window.localStorage.setItem('TAB_NAME_NOTIFI', tabName)
        }

        const previousTab = window.localStorage.getItem('TAB_NAME_NOTIFI');
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
