@extends('layout.master')

@section('content')
    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light"><a href="{{ route('contacts.index') }}">Contacts
                List</a>/</span>Add Contacts</h4>

    <!-- Form controls -->
    <div class="col-md-12">
        <div class="card mb-4">

            <form action="{{ route('contacts.store') }}" method="post" enctype="multipart/form-data">@csrf

                <div class="card-body">

                    <div class="mb-3">
                        <label class="col-sm-3 form-label">Spam</label>
                        <div class="col-sm-9">
                            <label class="switch">
                                <input type="checkbox" id="isSpam" name="isSpam" class="cbx hidden" checked />
                                <span class="slider round"></span>
                            </label>
                        </div>
                    </div>


                    <div class="mb-3">
                        <label for="image" class="form-label">Contacts image</label>
                        <input class="dropify form-control" name="image" type="file" id="image" />
                    </div>

                    <div class="mb-3">
                        <label for="url" class="form-label">Name</label>
                        <input type="text" required name="name" class="form-control" id="name"
                            placeholder="Enter name" />
                    </div>

                    <div class="mb-3">
                        <label for="phoneNumber" class="form-label">Phone Number</label>
                        <input type="number" required name="phoneNumber" class="form-control" id="phoneNumber"
                            placeholder="Enter phone number" />
                    </div>

                    <div class="mb-3">
                        <label for="spamType" class="form-label">Contact type</label>
                        <select class="form-select" required name="spamType" id="spamType"
                            aria-label="Default select example">

                            <option value="1">Business</option>
                            <option selected value="0">Person</option>

                        </select>
                    </div>


                    <div class="mb-3">
                        <label for="carrierName" class="form-label">Carrier Name</label>
                        <input type="text" name="carrierName" class="form-control" id="carrierName"
                            placeholder="Enter carrier name" />
                    </div>

                    <div class="mb-3">
                        <label for="countryName" class="form-label">Country Name</label>
                        <input type="text" name="countryName" class="form-control" id="countryName"
                            placeholder="Enter country name" />
                    </div>

                    <button type="submit" class="btn btn-primary">Submit</button>


                </div>
            </form>
        </div>
    </div>
@endsection
