<?php

namespace App\Http\Controllers;


use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;

class UserController extends Controller
{
    public function index()
    {
        return view('frontend.login');

    }

    public function login()
    {
        return view('frontend.login');
    }

    public function loginPost(Request $request)
    {


        $request->validate([
            'email' => 'required',
            'password' => 'required'
        ]);

        $credentails = $request->only('email', 'password');

        if (Auth::attempt($credentails)) {


            if (isset($request->remember) && !empty($request->remember)) {
                setcookie("email", $request['email'], time() + 3600);
                setcookie("password", $request['password'], time() + 3600);
            } else {
                setcookie("email", "");
                setcookie("password", "");
            }

            return redirect()->to('dashboard');
        }

        return redirect()->back()->with('errorMessage', 'worng email or password');
    }

    public function logout()
    {

        auth()->logout();
        return redirect()->route('login');

    }

    public function profile()
    {

        $user = Auth::user();
        return view('frontend.profile', compact('user'));


    }

    public function updateProfile(Request $request)
    {
        $request->validate([
            'user_name' => 'required|string|max:255|unique:users,name,' . Auth::id(),
            'user_email' => 'required|string|email|max:255|unique:users,email,' . Auth::id(),
            'user_password' => 'required|string|min:4|confirmed',
        ]);

        /** @var \App\Models\User $user */
        $user = Auth::user();

        // Update the user's information
        $user->name = $request->user_name;
        $user->email = $request->user_email;

        if (!empty($request->user_password)) {
            $user->password = Hash::make($request->user_password);
        }

        $user->save();
        return redirect()->route('profile')->with('successMessage', 'Profile updated successfully!');



    }
}
