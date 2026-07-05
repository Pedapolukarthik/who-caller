@extends('layout.master')

@section('content')
    @include('message')
    <h4 class="fw-bold py-3 mb-4">
        <span class="text-muted fw-light">
            <a href="{{ route('contacts.index') }}" class="text-decoration-none">Contacts List</a> /
        </span>
        Add Bulk Contacts
    </h4>

    <style>
        /* Custom Card Styling */
        .custom-card {
            background-color: #ffffff;
            /* Light Gray Background */

            /* Light Gray Border */
            border-radius: 10px;
            padding: 15px;
            box-shadow: 2px 4px 10px rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
        }

        .card-title {
            font-size: 18px;
            font-weight: bold;
            color: #333;
            margin-bottom: 10px;
        }

        /* Instructions Card Styling */
        .instruction-card {
            background-color: #fffbcc;
            /* Light Yellow */
            border: 2px solid #ffcc00;
            /* Yellow Border */
            border-radius: 10px;
            padding: 15px;
            box-shadow: 2px 4px 10px rgba(0, 0, 0, 0.1);
            margin-bottom: 15px;
        }

        .instruction-header {
            font-weight: bold;
            color: #333;
            display: flex;
            align-items: center;
            cursor: pointer;
        }

        .instruction-header i {
            color: #ff9900;
            /* Orange Bulb Icon */
            margin-right: 10px;
        }

        .instruction-body {
            display: none;
            margin-top: 10px;
            color: #444;
        }

        .instruction-card.show .instruction-body {
            display: block;
        }
    </style>

    <!-- First Card: Import Contacts (Excel File) -->
    <div class="custom-card">
        <div class="card-title">📂 Import Contacts (Excel File)</div>

        <!-- Instructions Section -->
        <div class="instruction-card" id="instructionCard">
            <div class="instruction-header" id="instructionToggle">
                <i class="bi bi-lightbulb-fill"></i> Instructions
                <span id="collapseIcon" style="margin-left: auto;">&#9660;</span> <!-- Down Arrow -->
            </div>
            <div class="instruction-body" id="instructions">
                <ul>
                    <li>Ensure the Excel file follows the correct format before uploading.</li>
                    <li>The first row should contain column headers: <b>Name</b> and <b>Number</b>.</li>
                    <li>Each row should contain a contact's name in the "Name" column and their phone number in the "Number"
                        column.</li>
                    <li>Example:
                        <br>
                        <code>
                            Name | Number <br>
                            John Doe | 1234567890 <br>
                            Jane Smith | 9876543210
                        </code>
                    </li>
                    <li>Accepted file formats: <b>.xls, .xlsx</b>.</li>
                </ul>
                <a href="{{ route('contacts.example') }}" class="btn btn-dark btn-sm">
                    <i class="bi bi-file-earmark-arrow-down"></i> Download Example File
                </a>
            </div>
        </div>

        <!-- Import Form -->
        <p class="text-muted">
            Select an Excel file (.xls or .xlsx) containing contact details to import them into the system. Ensure the
            format matches the expected structure.
        </p>
        <form action="{{ route('contacts.import') }}" method="POST" enctype="multipart/form-data">
            @csrf
            <div class="mb-3">
                <label for="file2" class="form-label">Select Excel File:</label>
                <input type="file" name="file2" id="file2" class="form-control" required accept=".xls,.xlsx">
                <button type="submit" class="btn btn-primary mt-3">
                    <i class="bi bi-upload"></i> Import
                </button>
            </div>
        </form>
    </div>

    <!-- Second Card: Import & Export Contacts (SQL File) -->
    <div class="custom-card">
        <div class="card-title">📂 Import & Export Contacts (SQL File)</div>

        <!-- Export Contacts Table -->
        <p class="text-muted">
            Click the button below to export all contacts as an SQL file. This allows you to back up the contacts table and
            restore it later using a database import.
        </p>
        <a href="{{ route('export.contacts.sql') }}" class="btn btn-success">
            <i class="bi bi-box-arrow-down"></i> Export Contacts Table (.sql)
        </a>

        <!-- Import Contacts Table -->
        <p class="text-muted mt-3">
            Upload an SQL file to import contacts into the database. Ensure the file is in the correct format before
            proceeding.
        </p>
        <form action="{{ route('import.contacts.sql') }}" method="POST" enctype="multipart/form-data" class="mt-2">
            @csrf
            <input type="file" name="sql_file" class="form-control mb-2 " required accept=".sql">
            <button type="submit" class="btn btn-primary mt-2">
                <i class="bi bi-upload"></i> Import Contacts Table (.sql)
            </button>
        </form>
    </div>

    <script>
        document.getElementById("instructionToggle").addEventListener("click", function() {
            var card = document.getElementById("instructionCard");
            var body = document.getElementById("instructions");
            var icon = document.getElementById("collapseIcon");

            card.classList.toggle("show");
            icon.innerHTML = card.classList.contains("show") ? "&#9650;" : "&#9660;"; // Toggle Arrow ↑ ↓
        });
    </script>
@endsection
