@extends('layout.master')

@section('content')
    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light"><a href="{{ route('contacts.create') }}">Add
                Contacts</a>/</span>Edit
        Contacts</h4>

    <!-- Form controls -->
    <div class="col-md-12">
        <div class="card mb-4">

            <form action="{{ route('contacts.update', $contacts->id) }}" method="POST" enctype="multipart/form-data">
                @csrf
                @method('PUT')

                <div class="card-body">


                    <div class="mb-3">
                        <label class="col-sm-3 form-label">Spam</label>
                        <div class="col-sm-9">
                            <label class="switch">
                                <input type="checkbox" id="isSpam" name="isSpam" class="cbx hidden"
                                    {{ $contacts->isSpam == 1 ? 'checked' : '' }} />
                                <span class="slider round"></span>
                            </label>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="image" class="form-label">Contact img</label>
                        <input class="dropify form-control"
                            data-default-file="{{ asset('public/' . str_replace('public/', 'storage/', $contacts->image)) }}"
                            name="image" type="file" id="image" />
                    </div>

                    <div class="mb-3">
                        <label for="name" class="form-label">Contact name</label>
                        <input value="{{ $contacts->name }}" type="text" name="name" class="form-control"
                            id="name" placeholder="Enter name" />
                    </div>

                    <div class="mb-3">
                        <label for="phoneNumber" class="form-label">Phone number</label>
                        <input value="{{ $contacts->phoneNumber }}" type="text" name="phoneNumber" class="form-control"
                            id="phoneNumber" placeholder="Enter number" />
                    </div>



                    <div class="mb-3">
                        <label for="spamType" class="form-label">Contact type</label>
                        <select class="form-select" required name="spamType" id="spamType"
                            aria-label="Default select example">
                            <option>Open this select menu</option>
                            <option {{ $contacts->spamType == 1 ? 'selected' : '' }} value="1">Business</option>
                            <option {{ $contacts->spamType == 0 ? 'selected' : '' }} value="0">Person</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label for="carrierName" class="form-label">Carrier name</label>
                        <input value="{{ $contacts->carrierName }}" type="text" name="carrierName" class="form-control"
                            id="carrierName" placeholder="Enter carrier name" />
                    </div>

                    <div class="mb-3">
                        <label for="countryName" class="form-label">Country name</label>
                        <input value="{{ $contacts->countryName }}" type="text" name="countryName" class="form-control"
                            id="countryName" placeholder="Enter country name" />
                    </div>

                    <button type="submit" class="btn btn-primary">Submit</button>


                </div>
            </form>
        </div>
    </div>
@endsection
