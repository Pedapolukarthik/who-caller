@extends('layout.master')

@section('content')
    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light"><a href="{{ route('contacts.create') }}">Add
                Contacts</a>/</span>Edit
        Contacts</h4>

    <!-- Form controls -->
    <div class="col-md-12">
        <div class="card mb-4">

            <form action="{{ route('users.update', $user->id) }}" method="POST" enctype="multipart/form-data">
                @csrf
                @method('PUT')

                <div class="card-body">




                    <div class="mb-3">
                        <label for="imgUrl" class="form-label">User img</label>
                        <input class="dropify form-control"
                            data-default-file="{{ asset('public/' . str_replace('public/', 'storage/', $user->imgUrl)) }}"
                            name="imgUrl" type="file" id="imgUrl" />
                    </div>

                    <div class="mb-3">
                        <label for="first_name" class="form-label">First name</label>
                        <input value="{{ $user->first_name }}" type="text" name="first_name" class="form-control"
                            id="first_name" placeholder="Enter name" />
                    </div>

                    <div class="mb-3">
                        <label for="last_name" class="form-label">Last name</label>
                        <input value="{{ $user->last_name }}" type="text" name="last_name" class="form-control"
                            id="last_name" placeholder="Enter name" />
                    </div>

                    <div class="mb-3">
                        <label for="phone" class="form-label">Phone number</label>
                        <input value="{{ $user->phone }}" type="text" name="phone" class="form-control" id="phone"
                            placeholder="Enter number" />
                    </div>



                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input value="{{ $user->email }}" type="email" name="email" class="form-control" id="email"
                            placeholder="Enter email" />
                    </div>



                    <button type="submit" class="btn btn-primary">Submit</button>


                </div>
            </form>
        </div>
    </div>
@endsection
