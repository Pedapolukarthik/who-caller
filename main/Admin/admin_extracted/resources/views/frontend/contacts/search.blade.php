@extends('layout.master')

@section('content')
    @include('message')

    <div id="errorMessage" class="alert alert-danger alert-dismissible fade" role="alert" style="display:none;">
        <span id="errorMessageText"></span>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>

    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light"><a
                href="{{ route('dashboard') }}">Dashboard</a>/</span>Search List</h4>

    <!-- Basic Bootstrap Table -->
    <div class="card">
        <div class="table-responsive text-nowrap">
            <table class="table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Name</th>
                        <th>Phone Number</th>
                        <th>Spam</th>
                        <th>Actions</th>
                    </tr>
                </thead>


                @foreach ($search_contacts as $key => $contact)
                    <tbody class="table-border-bottom-0">
                        <tr>
                            <td><i class="fab fa-angular fa-lg text-danger me-3"></i>
                                <strong>{{ ++$key }}</strong>
                            </td>

                            <td>{{ $contact->name }}</td>

                            <td>{{ $contact->phoneNumber }}</td>
                            <td><span
                                    class="badge rounded-pill {{ $contact->isSpam ? 'bg-danger' : 'bg-success' }}">{{ $contact->isSpam ? 'Spam' : 'Not Spam' }}</span>
                            </td>
                            <td>

                                <form id="delete_form" action="{{ route('contacts.destroy', $contact->id) }}"method="POST">
                                    @csrf
                                    @method('DELETE')
                                    <div class="d-flex">
                                        <a href="{{ route('contacts.edit', $contact->id) }}" class="me-2">
                                            <span class="badge bg-label-primary p-2"><i
                                                    class="bx bxs-edit text-primary"></i> Edit</span>
                                        </a>
                                        <div data-item-id="{{ $contact->id }}" class="deleteFunItem me-2">
                                            <span class="badge bg-label-danger p-2"><i class="bx bxs-trash text-danger"></i>
                                                Delete</span>
                                        </div>

                                    </div>
                                </form>

                            </td>
                        </tr>
                    </tbody>
                @endforeach
            </table>

            <!-- Pagination Links -->
            <div class="table">
                {{ $search_contacts->links('pagination::bootstrap-5') }}
            </div>

        </div>
    </div>
    <!--/ Basic Bootstrap Table -->
@endsection



@push('js')
    <script>
        $(function() {
            $('.deleteFunItem').click(function(event) {
                var demoMode = "{{ env('DEMO_MODE') }}"; // Get the DEMO_MODE status
                var form = $(this).closest('form'); // Define the form variable here

                if (demoMode) {
                    event.preventDefault(); // Stop the form from submitting
                    displayErrorMessage('Changes are not allowed in demo mode.'); // Show error message
                } else {
                    if (confirm("Are you sure you want to delete this contact?")) {
                        // Proceed with AJAX request if not in demo mode
                        $.ajax({
                            type: 'POST',
                            url: form.attr(
                                'action'), // Use the form associated with the delete button
                            data: form.serialize(), // Serialize the form data
                            success: function(response) {
                                // Handle the successful response from the server
                                console.log(response);
                                location
                                    .reload(); // Optionally, perform additional actions (e.g., redirect)
                                // window.location.href = "/redirect-url";
                            },
                            error: function(error) {
                                // Handle the error response from the server
                                displayErrorMessage(
                                    'An error occurred while attempting to delete the category.'
                                );
                            }
                        });
                    }
                }
            });

            function displayErrorMessage(message) {
                $('#errorMessageText').text(message);
                $('#errorMessage').show().addClass(
                    'show'); // Use Bootstrap classes to handle visibility and animation
            }

            // Optionally handle close button for the alert
            $('.btn-close').click(function() {
                $('#errorMessage').hide().removeClass('show');
                location
                    .reload(); // Only reload here if it is necessary, might not be needed depending on UX requirements
            });
        });
    </script>
@endpush
