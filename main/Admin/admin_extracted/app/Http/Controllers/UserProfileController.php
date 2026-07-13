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
        $request->validate([
            'phone' => 'required|string|max:20',
        ]);

        $phone = $request->input('phone');
        $userProfile = UserProfile::where('phone', $phone)->first();

        if (!$userProfile) {
            $userProfile = UserProfile::create([
                'phone' => $phone,
            ]);
        }

        return response()->json(['message' => 'User profile created successfully', 'user' => $userProfile], 201);
    }

    public function registerEmail(Request $request)
    {
        $request->validate([
            'first_name' => 'required|string|max:255',
            'email' => 'required|string|email|max:255',
            'phone' => 'required|string|max:20',
        ]);

        $email = $request->input('email');
        $phone = $request->input('phone');
        $firstName = $request->input('first_name');

        // Check if email is already taken by a different user profile
        $userByEmail = UserProfile::where('email', $email)->first();
        if ($userByEmail && $userByEmail->phone !== $phone) {
            return response()->json([
                'message' => 'The email has already been taken.',
                'errors' => ['email' => ['The email has already been taken.']]
            ], 422);
        }

        // Check if phone is already registered
        $userByPhone = UserProfile::where('phone', $phone)->first();

        if ($userByPhone) {
            // Update the existing phone record with email and first name
            $userByPhone->email = $email;
            $userByPhone->first_name = $firstName;
            $userByPhone->save();
            $userProfile = $userByPhone;
        } else if ($userByEmail) {
            // Update the existing email record with phone and first name
            $userByEmail->phone = $phone;
            $userByEmail->first_name = $firstName;
            $userByEmail->save();
            $userProfile = $userByEmail;
        } else {
            // Create a new profile
            $userProfile = UserProfile::create([
                'first_name' => $firstName,
                'email' => $email,
                'phone' => $phone,
            ]);
        }

        return response()->json(['message' => 'User profile created successfully', 'user' => $userProfile], 201);
    }

    public function registerGoogle(Request $request)
    {
        try {
            $request->validate([
                'first_name' => 'required|string|max:255',
                'email' => 'required|string|email|max:255',
                'imgUrl' => 'nullable|string|max:1000',
            ]);

            $email = $request->input('email');
            $userProfile = UserProfile::where('email', $email)->first();

            if (!$userProfile) {
                $userProfile = UserProfile::create([
                    'first_name' => $request->input('first_name'),
                    'email' => $email,
                    'imgUrl' => $request->input('imgUrl'),
                ]);
            }

            return response()->json(['message' => 'User profile created successfully', 'user' => $userProfile], 201);

        } catch (\Exception $e) {
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
        try {
            $phoneNumber = $request->input('phone');
            $user = UserProfile::where('phone', $phoneNumber)->first();

            $email = $request->input('email');
            $user2 = UserProfile::where('email', $email)->first();

            if ($user && $user2 && $user->id !== $user2->id) {
                // Both profiles exist and are different.
                // We merge $user2 (email/Google profile) into $user (phone profile).
                // First delete $user2 to avoid duplicate email/phone unique constraint conflicts.
                \Illuminate\Support\Facades\DB::transaction(function () use ($user, $user2, $request) {
                    $user2->delete();

                    $user->first_name = $request->input('first_name') ?: $user->first_name;
                    $user->last_name = $request->input('last_name') ?: $user->last_name;
                    $user->email = $request->input('email');
                    $user->imgUrl = $request->input('imgUrl') ?: $user->imgUrl;
                    $user->phone = $request->input('phone');
                    $user->save();
                });

                return response()->json(['status' => 'success', 'message' => 'Profile updated successfully'], 200);
            } else if ($user) {
                $user->first_name = $request->input('first_name');
                $user->last_name = $request->input('last_name');
                $user->email = $request->input('email');
                $user->imgUrl = $request->input('imgUrl');
                $user->phone = $request->input('phone');
                // Update other fields as necessary

                $user->save();

                return response()->json(['status' => 'success', 'message' => 'Profile updated successfully'], 200);
            } else if ($user2) {
                $user2->first_name = $request->input('first_name');
                $user2->last_name = $request->input('last_name');
                $user2->email = $request->input('email');
                $user2->imgUrl = $request->input('imgUrl');
                $user2->phone = $request->input('phone');

                $user2->save();

                return response()->json(['status' => 'success', 'message' => 'Profile updated successfully'], 200);
            } else {
                // Self-healing: if the profile does not exist at all in the database, create it now
                $newUser = UserProfile::create([
                    'first_name' => $request->input('first_name'),
                    'last_name' => $request->input('last_name'),
                    'email' => $request->input('email'),
                    'phone' => $request->input('phone'),
                    'imgUrl' => $request->input('imgUrl'),
                ]);
                return response()->json(['status' => 'success', 'message' => 'Profile created and updated successfully', 'user' => $newUser], 200);
            }
        } catch (\Exception $e) {
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
