@extends('layout.master')

@section('content')


    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light"><a
                href="{{ route('dashboard') }}">Dashboard</a>/</span>Profile</h4>
    @include('message')
    @if ($errors->any())
        @foreach ($errors->all() as $error)
            <div class="alert alert-danger">{{ $error }}</div>
        @endforeach
    @endif
    <div class="col-md-12">
        <div class="card mb-4">

            <form action="{{ route('update.profile') }}" method="post" enctype="multipart/form-data">@csrf

                <div class="card-body">


                    <h5 class="mb-3">Edit Adminstrator</h5>

                    <div class="mb-3">
                        <label for="user_name" class="form-label">Username</label>
                        <input type="text" value="{{ $user->name }}" min="1" required name="user_name"
                            class="form-control" id="user_name" />
                    </div>


                    <div class="mb-3">
                        <label for="user_email" class="form-label">Email</label>
                        <input type="email" value="{{ $user->email }}" min="1" required name="user_email"
                            class="form-control" id="user_email" />
                    </div>

                    <div class="mb-3">
                        <label for="user_password" class="form-label">Password</label>
                        <input type="password" value="" min="1" required name="user_password"
                            class="form-control" id="user_password" />
                    </div>


                    <div class="mb-3">
                        <label for="user_password_confirmation" class="form-label">Re-enter Password</label>
                        <input type="password" value="" min="1" required name="user_password_confirmation"
                            class="form-control" id="user_password_confirmation" />
                    </div>



                    <button type="submit" class="btn btn-primary">Update</button>


                </div>
            </form>
        </div>
    </div>
@endsection
