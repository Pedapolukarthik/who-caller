@if (Session::has('successMessage'))
    <div class="alert alert-success alert-dismissible" role="alert">
        {{ Session::get('successMessage') }}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
@endif


@if (Session::has('errorMessage'))
    <div class="alert alert-danger alert-dismissible" role="alert">
        {{ Session::get('errorMessage') }}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
@endif
