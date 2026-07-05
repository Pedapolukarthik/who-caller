@extends('layout.master')

@section('content')
    @include('message')

    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light"><a href="{{ route('dashboard') }}">Dashboard</a>/</span>App
        Update</h4>

    @if ($errors->any())
        @foreach ($errors->all() as $error)
            <div class="alert alert-danger">{{ $error }}</div>
        @endforeach
    @endif
    <div class="col-md-12">
        <div class="card mb-4">

            <form action="{{ route('update.post') }}" method="post" enctype="multipart/form-data">@csrf

                <div class="card-body">


                    <div class="mb-3">
                        <label class="col-sm-3 form-label">ON/OFF</label>
                        <div class="col-sm-9">
                            <label class="switch">
                                <input type="checkbox" id="app_update_status" name="app_update_status" class="cbx hidden"
                                    {{ $setting->app_update_status == 1 ? 'checked' : '' }} />
                                <span class="slider round"></span>
                            </label>
                        </div>
                    </div>


                    <div class="mb-3">
                        <label for="app_new_version" class="form-label">New App Version Code</label>
                        <input type="number" value="{{ $setting->app_new_version }}" min="1" required
                            name="app_new_version" class="form-control" id="app_new_version" />
                    </div>

                    <div class="mb-3">
                        <label for="app_update_desc" class="form-label">Description</label>
                        <textarea required class="form-control" name="app_update_desc" rows="3">{{ stripslashes($setting->app_update_desc) }}</textarea>
                    </div>



                    <div class="mb-3">
                        <label for="app_redirect_url" class="form-label">App Link</label>
                        <input type="url" value="{{ $setting->app_redirect_url }}" required name="app_redirect_url"
                            class="form-control" id="app_redirect_url" />
                    </div>



                    <button type="submit" class="btn btn-primary">Save</button>


                </div>
            </form>
        </div>
    </div>
@endsection
