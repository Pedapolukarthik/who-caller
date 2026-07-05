@extends('layout.master')


@section('content')
    @php
        $contacts = \App\Models\Contacts::get();
        $contactsCount = count($contacts);

        $users = \App\Models\UserProfile::get();
        $usersCount = count($users);

        use Carbon\Carbon;
        $currentYear = Carbon::now()->year;

        // Data for Monthly Post Creation Chart
        $contactsMonthlyData = \App\Models\Contacts::selectRaw('MONTH(created_at) as month, COUNT(*) as count')
            ->whereYear('created_at', $currentYear)
            ->groupBy('month')
            ->get()
            ->pluck('count', 'month');

        $userMonthlyData = \App\Models\UserProfile::selectRaw('MONTH(created_at) as month, COUNT(*) as count')
            ->whereYear('created_at', $currentYear)
            ->groupBy('month')
            ->get()
            ->pluck('count', 'month');

        $months = collect(range(1, 12))->map(function ($month) {
            return Carbon::create()->month($month)->format('F');
        });

        $contactsCounts = $months->map(function ($month, $index) use ($contactsMonthlyData) {
            return $contactsMonthlyData[$index + 1] ?? 0;
        });

        $userCounts = $months->map(function ($month, $index) use ($userMonthlyData) {
            return $userMonthlyData[$index + 1] ?? 0;
        });

        $normalCount = \App\Models\Contacts::where('isSpam', [0])->count();
        $spamCount = \App\Models\Contacts::where('isSpam', [1])->count();

    @endphp

    <h4 class="fw-bold py-3 mb-4"><span class="text-muted fw-light">Dashboard /</span> Dashboard</h4>


    <div class="row mb-2">

        <div class="col-md-6 col-lg-4 mb-3">
            <a href="{{ route('contacts.index') }}">
                <div class="card bg-primary text-white">
                    <br>
                    <div class="text-center m-3">

                        <div style="font-size: 20px;" class="text-white text-uppercase">Contacts</div>
                        <div><i class="menu-icon tf-icons bx bxs-carousel"></i></div>
                        <div class="mt-1">Total {{ $contactsCount }} Contacts</div>
                    </div>
                    <br>
                </div>
            </a>
        </div>

        <div class="col-md-6 col-lg-4 mb-3">
            <a href="{{ route('users.index') }}">
                <div class="card bg-success text-white">
                    <br>
                    <div class="text-center m-3">

                        <div style="font-size: 20px;" class="text-white text-uppercase">Users</div>
                        <div><i class="menu-icon tf-icons bx bxs-category"></i></div>
                        <div class="mt-1">Total {{ $usersCount }} Users</div>
                    </div>
                    <br>
                </div>
            </a>
        </div>

        <div class="col-md-6 col-lg-4 mb-3">
            <a href="{{ route('settings') }}">
                <div class="card bg-secondary text-white">
                    <br>
                    <div class="text-center m-3">

                        <div style="font-size: 20px;" class="text-white text-uppercase">Settings</div>
                        <div><i class="menu-icon tf-icons bx bxs-cog"></i></div>
                        <div class="mt-1">App Settings and Privacy</div>
                    </div>
                    <br>
                </div>
            </a>
        </div>

    </div>


    <div class="card mb-4 pb-3">
        <div class="card-body">

            <div class="row">
                <!-- User Registration Monthly Chart -->
                <div class="col-md-6 mt-5">
                    <h5 class="text-center">Monthly User Registrations in {{ $currentYear }}</h5>
                    <canvas id="userRegistrationChart" height="100"></canvas>
                    <!-- Posts Created Monthly Chart -->
                    <h5 class="text-center mt-5">Monthly Contacts Created in {{ $currentYear }}</h5>
                    <canvas id="monthlyPostsChart" height="100"></canvas>
                </div>




                <!-- Post Status Doughnut Chart -->
                <div class="col-md-6 mt-5">
                    <h5 class="text-center">Contacts Status Distribution</h5>
                    <canvas id="postStatusChart" height="100"></canvas>
                </div>
            </div>

        </div>
    </div>




    <script>
        // Monthly User Registration Chart
        const userMonthlyData = {!! json_encode($userCounts) !!};
        const userLabels = {!! json_encode($months) !!};

        const userRegistrationConfig = {
            type: 'bar',
            data: {
                labels: userLabels,
                datasets: [{
                    label: 'User Registrations',
                    data: userMonthlyData,
                    backgroundColor: 'rgba(255, 159, 64, 0.2)',
                    borderColor: 'rgba(255, 159, 64, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        };

        // Monthly Post Creation Chart
        const postMonthlyData = {!! json_encode($contactsCounts) !!};
        const postLabels = {!! json_encode($months) !!};

        const postCreationConfig = {
            type: 'bar',
            data: {
                labels: postLabels,
                datasets: [{
                    label: 'Contacts Created',
                    data: postMonthlyData,
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    borderColor: 'rgba(75, 192, 192, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        };

        // Doughnut Chart for Post Status
        const postStatusData = {
            labels: ['Normal', 'Spam'],
            datasets: [{
                data: [{{ $normalCount }}, {{ $spamCount }}],
                backgroundColor: [
                    'rgba(54, 162, 235, 0.2)',
                    'rgba(255, 99, 132, 0.2)'
                ],
                borderColor: [
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 99, 132, 1)'
                ],
                borderWidth: 1
            }]
        };

        const postStatusConfig = {
            type: 'doughnut',
            data: postStatusData,
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'top',
                    },
                },
            },
        };

        // Render Charts
        window.onload = function() {
            const ctxUser = document.getElementById('userRegistrationChart').getContext('2d');
            new Chart(ctxUser, userRegistrationConfig);

            const ctxPost = document.getElementById('monthlyPostsChart').getContext('2d');
            new Chart(ctxPost, postCreationConfig);

            const ctxStatus = document.getElementById('postStatusChart').getContext('2d');
            new Chart(ctxStatus, postStatusConfig);
        };
    </script>
@endsection
