<?php

namespace App\Http\Controllers;


use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Routing\Redirector;
use Illuminate\Support\Facades\DB;
use Illuminate\Contracts\View\View;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\URL;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Http;
use Illuminate\Http\RedirectResponse;
use Illuminate\Contracts\View\Factory;
use Illuminate\Support\Facades\Artisan;
use Illuminate\Contracts\Foundation\Application;

class InstallController extends Controller
{
    // use ActivationClass, UnloadedHelpers;


    public function step0()
    {
        Log::info('Installing');

        return view('installation.step0');
    }

    public function step1(Request $request): View|Factory|RedirectResponse|Application
    {

        if (Hash::check('step_1', $request['token'])) {
            $permission['curl_enabled'] = function_exists('curl_version');
            $permission['db_file_write_perm'] = is_writable(base_path('.env'));
            $permission['routes_file_write_perm'] = is_writable(base_path('app/Providers/RouteServiceProvider.php'));

            return view('installation.step1', compact('permission'));
        }
        session()->flash('error', 'Access denied!');

        return redirect()->route('step0');
    }



    public function step2(Request $request): View|Factory|RedirectResponse|Application
    {
        if (Hash::check('step_2', $request['token'])) {
            return view('installation.step2');
        }
        session()->flash('error', 'Access denied!');
        return redirect()->route('step0');
    }

    public function step3(Request $request): View|Factory|RedirectResponse|Application
    {
        if (Hash::check('step_3', $request['token'])) {
            return view('installation.step3');
        }
        session()->flash('error', 'Access denied!');
        return redirect()->route('step0');
    }

    public function step4(Request $request): View|Factory|RedirectResponse|Application
    {
        if (Hash::check('step_4', $request['token'])) {
            return view('installation.step4');
        }
        session()->flash('error', 'Access denied!');
        return redirect()->route('step0');
    }

    public function step5(Request $request): View|Factory|RedirectResponse|Application
    {
        if (Hash::check('step_5', $request['token'])) {
            return view('installation.step5');
        }
        session()->flash('error', 'Access denied!');
        return redirect()->route('step0');
    }


    public function save_data(Request $request): RedirectResponse
    {

        $request->validate([
            base64_decode('cHVyY2hhc2Vfa2V5') => 'required|string',
            base64_decode('dXNlcm5hbWU=') => 'required|string',
        ]);

        $post = [
            base64_decode('dXNlcm5hbWU=') => $request[base64_decode('dXNlcm5hbWU=')],
            base64_decode('cHVyY2hhc2Vfa2V5') => $request[base64_decode('cHVyY2hhc2Vfa2V5')],
            base64_decode('ZG9tYWlu') => preg_replace("#^[^:/.]*[:/]+#i", "", url('/')),
        ];

        try {
            $response = Http::withToken('kjgvAZLDPsyx7f1o4cYtwjqOLKwpditp')
                ->get(base64_decode('aHR0cHM6Ly9hcGkuZW52YXRvLmNvbS92My9tYXJrZXQvYXV0aG9yL3NhbGU='), [
                    base64_decode('Y29kZQ==') => $request[base64_decode('cHVyY2hhc2Vfa2V5')]
                ]);

            if ($response->successful()) {
                $data = $response->json();

                if ($data[base64_decode('aXRlbQ==')][base64_decode('aWQ=')] == '54075048') {
                    if ($data[base64_decode('YnV5ZXI=')] === $request[base64_decode('dXNlcm5hbWU=')]) {
                        $this->dmvf($post);
                        return redirect()->route(base64_decode('c3RlcDM='), ['token' => bcrypt(base64_decode('c3RlcF8z'))]);
                    }
                    session()->flash(base64_decode('ZXJyb3I='), base64_decode('UHVyY2hhc2UgdGVzdGVkLiBCdXQgdXNlcm5hbWUgbWlzbWF0Y2gh'));
                    return redirect()->route(base64_decode('c3RlcDI='), ['token' => bcrypt(base64_decode('c3RlcF8y'))]);
                } else {
                    session()->flash(base64_decode('ZXJyb3I='), base64_decode('SW52YWxpZCBwdXJjaGFzZSBkZXRhaWxzIG9yIGl0ZW0gbWlzbWF0Y2gu'));
                    return redirect()->route(base64_decode('c3RlcDI='), ['token' => bcrypt(base64_decode('c3RlcF8y'))]);
                }
            } else {
                session()->flash(base64_decode('ZXJyb3I='), base64_decode('SW52YWxpZCBwdXJjaGFzZSBkZXRhaWxzIG9yIFNlcnZlciBlcnJvcg=='));
                return redirect()->route(base64_decode('c3RlcDI='), ['token' => bcrypt(base64_decode('c3RlcF8y'))]);
            }
        } catch (\Exception $e) {
            session()->flash(base64_decode('ZXJyb3I='), base64_decode('SW52YWxpZCBwdXJjaGFzZSBkZXRhaWxzIG9yIFNlcnZlciBlcnJvcg=='));
            return redirect()->route(base64_decode('c3RlcDI='), ['token' => bcrypt(base64_decode('c3RlcF8y'))]);
        }
    }



    public function system_settings(Request $request): View|Factory|RedirectResponse|Application
    {
        if (!Hash::check('step_6', $request['token'])) {
            session()->flash('error', 'Access denied!');
            return redirect()->route('step0');
        }


        User::create([
            'name' => $request['first_name'],
            'email' => $request['email'],
            'password' => bcrypt($request['password']),
            'created_at' => now(),
            'updated_at' => now()
        ]);

        $previousRouteServiceProvier = base_path('app/Providers/RouteServiceProvider.php');
        $newRouteServiceProvier = base_path('app/Providers/RouteServiceProvider.txt');
        copy($newRouteServiceProvier, $previousRouteServiceProvier);

        Artisan::call('optimize:clear');

        return redirect()->to(env('APP_URL'));

    }

    public function database_installation(Request $request): Redirector|Application|RedirectResponse
    {
        Log::info('database_installation', ['request_data' => $request->all()]);

        if (self::check_database_connection($request->DB_HOST, $request->DB_DATABASE, $request->DB_USERNAME, $request->DB_PASSWORD)) {

            $key = base64_encode(random_bytes(32));
            $output =
                'APP_NAME=Whocaller
APP_ENV=live
APP_KEY=base64:' . $key . '
APP_DEBUG=false
APP_URL=' . URL::to('/') . '


LOG_CHANNEL=stack
LOG_DEPRECATIONS_CHANNEL=null
LOG_LEVEL=debug
DEMO_MODE=false


DB_CONNECTION=mysql
DB_HOST=' . $request->DB_HOST . '
DB_PORT=3306
DB_DATABASE=' . $request->DB_DATABASE . '
DB_USERNAME=' . $request->DB_USERNAME . '
DB_PASSWORD=' . $request->DB_PASSWORD . '


BROADCAST_DRIVER=log
CACHE_DRIVER=file
FILESYSTEM_DISK=local
QUEUE_CONNECTION=sync
SESSION_DRIVER=file
SESSION_LIFETIME=120
MEMCACHED_HOST=127.0.0.1

REDIS_HOST=127.0.0.1
REDIS_PASSWORD=null
REDIS_PORT=6379

MAIL_MAILER=smtp
MAIL_HOST=mailpit
MAIL_PORT=1025
MAIL_USERNAME=null
MAIL_PASSWORD=null
MAIL_ENCRYPTION=null
MAIL_FROM_ADDRESS="hello@example.com"
MAIL_FROM_NAME="${APP_NAME}"

AWS_ACCESS_KEY_ID=
AWS_SECRET_ACCESS_KEY=
AWS_DEFAULT_REGION=us-east-1
AWS_BUCKET=
AWS_USE_PATH_STYLE_ENDPOINT=false

PUSHER_APP_ID=
PUSHER_APP_KEY=
PUSHER_APP_SECRET=
PUSHER_HOST=
PUSHER_PORT=443
PUSHER_SCHEME=https
PUSHER_APP_CLUSTER=mt1

VITE_PUSHER_APP_KEY="${PUSHER_APP_KEY}"
VITE_PUSHER_HOST="${PUSHER_HOST}"
VITE_PUSHER_PORT="${PUSHER_PORT}"
VITE_PUSHER_SCHEME="${PUSHER_SCHEME}"
VITE_PUSHER_APP_CLUSTER="${PUSHER_APP_CLUSTER}"
';
            $file = fopen(base_path('.env'), 'w');
            fwrite($file, $output);
            fclose($file);

            $path = base_path('.env');
            if (file_exists($path)) {
                return redirect()->route('step4', ['token' => $request['token']]);
            } else {
                session()->flash('error', 'Database error!');
                return redirect()->route('step3', ['token' => bcrypt('step_3')]);
            }
        } else {
            session()->flash('error', 'Database host error!');
            return redirect()->route('step3', ['token' => bcrypt('step_3')]);
        }
    }



    public function import_sql(): Redirector|RedirectResponse|Application
    {
        try {
            $sql_path = base_path('installation/backup/database.sql');
            DB::unprepared(file_get_contents($sql_path));

            Log::info('database_installation', ['base_path import_sql' => $sql_path]);

            return redirect()->route('step5', ['token' => bcrypt('step_5')]);
        } catch (\Exception $exception) {
            session()->flash('error', 'Your database is not clean, do you want to clean database then import?');
            return back();
        }
    }

    public function force_import_sql(): Redirector|RedirectResponse|Application
    {
        try {
            Artisan::call('db:wipe');
            $sql_path = base_path('installation/backup/database.sql');
            Log::info('database_installation', ['base_path force_import_sql' => $sql_path]);
            DB::unprepared(file_get_contents($sql_path));

            return redirect()->route('step5', ['token' => bcrypt('step_5')]);
        } catch (\Exception $exception) {
            session()->flash('error', 'Check your database permission!');
            return back();
        }
    }

    function check_database_connection($db_host = "", $db_name = "", $db_user = "", $db_pass = ""): bool
    {
        try {
            if (@mysqli_connect($db_host, $db_user, $db_pass, $db_name)) {
                return true;
            } else {
                return false;
            }
        } catch (\Exception $exception) {
            return false;
        }
    }





    public function dmvf($request)
    {
        session()->put(base64_decode('cHVyY2hhc2Vfa2V5'), $request[base64_decode('cHVyY2hhc2Vfa2V5')]); //pk
        session()->put(base64_decode('dXNlcm5hbWU='), $request[base64_decode('dXNlcm5hbWU=')]); //un
        return base64_decode('c3RlcDM='); //s3
    }
}
