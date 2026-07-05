<?php

namespace App\Http\Controllers;

use App\Models\UserProfile;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;

class UserProfileController extends Controller
{

    protected $demoMode;
    public function __construct()
    {
        $this->demoMode = env('DEMO_MODE');
    }

    public function getImagePath(Request $request)
    {

        $completeFileName = $request->file('image')->getClientOriginalName();
        $fileNameOnly = pathinfo($completeFileName, PATHINFO_FILENAME);
        $extension = $request->file('image')->getClientOriginalExtension();

        $compPic = str_replace(' ', '_', $fileNameOnly) . '_' . time() . '.' . $extension;

        return $request->file('image')->storeAs('public/profile', $compPic);
    }
    public function index()
    {
        $users = UserProfile::orderBy('created_at', 'desc')->paginate(10);
        return view('frontend.appUsers.index', compact('users'));
    }


    public function update(Request $request, string $id)
    {

        if ($this->demoMode == 'true') {
            return redirect(route('users.index'))->with('errorMessage', 'Demo mode is enabled. Changes are not allowed.');

        } else {
            $user = UserProfile::find($id);

            $request->validate([
                'first_name' => 'required',
                'phone' => 'required',

            ]);

            if ($request->hasFile('imgUrl')) {
                $image_path = $this->getImagePath($request);
                $user->imgUrl = $image_path;
            }

            $user->first_name = $request->first_name;
            $user->last_name = $request->last_name;
            $user->email = $request->email;
            $user->phone = $request->phone;


            $user->save();

            return redirect(route('users.index'))->with('successMessage', 'User Edited successfully');
        }

    }



    public function edit(string $id)
    {

        if ($this->demoMode == 'true') {
            return redirect(route('users.index'))->with('errorMessage', 'Demo mode is enabled. Changes are not allowed.');

        } else {
            $user = UserProfile::where('id', $id)->first();
            return view('frontend.appUsers.edit_users', compact('user'));
        }

    }

    public function destroy(string $id)
    {
        if ($this->demoMode == 'true') {
            return redirect(route('users.index'))->with('errorMessage', 'Demo mode is enabled. Changes are not allowed.');

        } else {
            UserProfile::find($id)->delete();
            return redirect(route('frontend.appUsers.index'))->with('successMessage', 'User Deleted successfully');
        }


    }
    public function registerPhone(Request $request)
    {

        $validatedData = $request->validate([
            'phone' => 'required|string|max:15|unique:user_profiles',
        ]);

        $userProfile = UserProfile::create($validatedData);

        return response()->json(['message' => 'User profile created successfully', 'user' => $userProfile], 201);
    }

    public function registerEmail(Request $request)
    {


        $validatedData = $request->validate([
            'first_name' => 'required|string|max:255',
            'email' => 'required|string|email|max:255|unique:user_profiles',
            'phone' => 'required|string|max:20',

        ]);

        $userProfile = UserProfile::create($validatedData);


        return response()->json(['message' => 'User profile created successfully', 'user' => $userProfile], 201);
    }

    public function registerGoogle(Request $request)
    {
        Log::info('Received registration request', ['request_data' => $request->all()]);
        try {
            $validatedData = $request->validate([
                'first_name' => 'required|string|max:255',
                'email' => 'required|string|email|max:255|unique:user_profiles',
                'imgUrl' => 'string|max:1000',

            ]);

            $userProfile = UserProfile::create($validatedData);

            return response()->json(['message' => 'User profile created successfully', 'user' => $userProfile], 201);

        } catch (\Exception $e) {
            Log::error('registerGoogle error', ['message' => 'Registration failed', 'error' => $e->getMessage()]);
            return response()->json(['message' => 'Registration failed', 'error' => $e->getMessage()], 500);
        }
    }


    public function getProfile(Request $request)
    {

        $phoneNumber = $request->input('phone');
        $email = $request->input('email');

        if (!empty($phoneNumber)) {
            $user = UserProfile::where('phone', $phoneNumber)->first();
        } elseif (!empty($email)) {
            $user = UserProfile::where('email', $email)->first();
        } else {
            $user = null;
        }


        if ($user) {
            return response()->json([
                'status' => 'success',
                'data' => $user
            ], 200);
        } else {
            return response()->json([
                'status' => 'error',
                'message' => 'User not found'
            ], 404);
        }
    }

    public function updateProfile(Request $request)
    {
        //Log::info('Received registration request', ['request_data' => $request->all()]);

        try {
            $phoneNumber = $request->input('phone');
            $user = UserProfile::where('phone', $phoneNumber)->first();

            $email = $request->input('email');
            $user2 = UserProfile::where('email', $email)->first();

            if ($user) {
                $user->first_name = $request->input('first_name');
                $user->last_name = $request->input('last_name');
                $user->email = $request->input('email');
                $user->imgUrl = $request->input('imgUrl');
                $user->phone = $request->input('phone');
                // Update other fields as necessary

                $user->save();

                //Log::info('Profile updated successfully', ['user_id' => $user->id]);

                return response()->json(['status' => 'success', 'message' => 'Profile updated successfully'], 200);
            } else if ($user2) {
                $user2->first_name = $request->input('first_name');
                $user2->last_name = $request->input('last_name');
                $user2->email = $request->input('email');
                $user2->imgUrl = $request->input('imgUrl');
                $user2->phone = $request->input('phone');

                $user2->save();

                //Log::info('Profile updated successfully', ['user_id' => $user2->id]);

                return response()->json(['status' => 'success', 'message' => 'Profile updated successfully'], 200);
            } else {
                //Log::warning('User not found', ['request_data' => $request->all()]);

                return response()->json(['status' => 'error', 'message' => 'User not found'], 404);
            }
        } catch (\Exception $e) {
            //Log::error('Error updating profile', ['error' => $e->getMessage(), 'request_data' => $request->all()]);

            return response()->json(['status' => 'error', 'message' => 'An error occurred while updating the profile'], 500);
        }
    }


    public function uploadImage(Request $request)
    {

        if ($request->hasFile('image')) {
            $image_path = $this->getImagePath($request);
            return response()->json(['path' => $image_path], 200);
        }

        return response()->json(['error' => 'No file uploaded'], 400);
    }
}
