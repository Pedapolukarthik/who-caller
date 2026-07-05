-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 15, 2024 at 04:15 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `whocaller`
--

-- --------------------------------------------------------

--
-- Table structure for table `ads`
--

CREATE TABLE `ads` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `ad_status` bigint(20) DEFAULT NULL,
  `main_ads` varchar(255) DEFAULT NULL,
  `admob_publisher_id` varchar(255) DEFAULT NULL,
  `admob_banner_unit_id` varchar(255) DEFAULT NULL,
  `admob_interstitial_unit_id` varchar(255) DEFAULT NULL,
  `admob_native_unit_id` varchar(255) DEFAULT NULL,
  `admob_app_open_unit_id` varchar(255) DEFAULT NULL,
  `unity_game_id` varchar(255) DEFAULT NULL,
  `unity_banner_placement_id` varchar(255) DEFAULT NULL,
  `unity_interstitial_placement_id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `ads`
--

INSERT INTO `ads` (`id`, `created_at`, `updated_at`, `ad_status`, `main_ads`, `admob_publisher_id`, `admob_banner_unit_id`, `admob_interstitial_unit_id`, `admob_native_unit_id`, `admob_app_open_unit_id`, `unity_game_id`, `unity_banner_placement_id`, `unity_interstitial_placement_id`) VALUES
(1, NULL, '2024-07-16 08:04:29', 1, 'admob', NULL, 'ca-app-pub-3940256099942544/6300978111', 'ca-app-pub-3940256099942544/1033173712', 'ca-app-pub-3940256099942544/2247696110', 'ca-app-pub-3940256099942544/3419835294', '4089993', 'banner', 'video');

-- --------------------------------------------------------

--
-- Table structure for table `contacts`
--

CREATE TABLE `contacts` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `phoneNumber` varchar(255) NOT NULL,
  `isSpam` tinyint(1) NOT NULL DEFAULT 0,
  `spamType` varchar(255) DEFAULT NULL,
  `tag` varchar(255) DEFAULT NULL,
  `carrierName` varchar(255) DEFAULT NULL,
  `countryName` varchar(255) DEFAULT NULL,
  `appuser_id` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `failed_jobs`
--

CREATE TABLE `failed_jobs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `connection` text NOT NULL,
  `queue` text NOT NULL,
  `payload` longtext NOT NULL,
  `exception` longtext NOT NULL,
  `failed_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `migrations`
--

CREATE TABLE `migrations` (
  `id` int(10) UNSIGNED NOT NULL,
  `migration` varchar(255) NOT NULL,
  `batch` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `migrations`
--

INSERT INTO `migrations` (`id`, `migration`, `batch`) VALUES
(1, '2014_10_12_000000_create_users_table', 1),
(2, '2014_10_12_100000_create_password_reset_tokens_table', 1),
(3, '2019_08_19_000000_create_failed_jobs_table', 1),
(4, '2019_12_14_000001_create_personal_access_tokens_table', 1),
(5, '2023_10_23_163355_create_settings_table', 1),
(6, '2023_10_30_143001_create_ads_table', 1),
(7, '2023_11_14_154734_create_notifications_table', 1),
(8, '2024_06_09_094628_create_contacts_table', 1),
(9, '2024_06_09_100115_create_app_users_table', 1),
(10, '2024_06_09_154653_create_user_profiles_table', 1),
(11, '2024_06_27_054425_create_spam_table', 1);

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `title` varchar(255) NOT NULL,
  `message` varchar(255) NOT NULL,
  `image` varchar(255) NOT NULL,
  `url` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `password_reset_tokens`
--

CREATE TABLE `password_reset_tokens` (
  `email` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `personal_access_tokens`
--

CREATE TABLE `personal_access_tokens` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `tokenable_type` varchar(255) NOT NULL,
  `tokenable_id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `token` varchar(64) NOT NULL,
  `abilities` text DEFAULT NULL,
  `last_used_at` timestamp NULL DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `settings`
--

CREATE TABLE `settings` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `app_status` bigint(20) DEFAULT NULL,
  `onesignal_app_id` varchar(255) DEFAULT NULL,
  `onesignal_rest_key` varchar(255) DEFAULT NULL,
  `more_apps_url` varchar(255) DEFAULT NULL,
  `privacy_policy` text DEFAULT NULL,
  `isMaintenance` bigint(20) DEFAULT NULL,
  `app_update_status` bigint(20) DEFAULT NULL,
  `app_new_version` varchar(255) DEFAULT NULL,
  `app_update_desc` varchar(255) DEFAULT NULL,
  `app_redirect_url` varchar(255) DEFAULT NULL,
  `app_email` varchar(255) DEFAULT NULL,
  `app_author` varchar(255) DEFAULT NULL,
  `app_contact` varchar(255) DEFAULT NULL,
  `app_website` varchar(255) DEFAULT NULL,
  `app_developed_by` varchar(255) DEFAULT NULL,
  `app_description` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `settings`
--

INSERT INTO `settings` (`id`, `app_status`, `onesignal_app_id`, `onesignal_rest_key`, `more_apps_url`, `privacy_policy`, `isMaintenance`, `app_update_status`, `app_new_version`, `app_update_desc`, `app_redirect_url`, `app_email`, `app_author`, `app_contact`, `app_website`, `app_developed_by`, `app_description`, `created_at`, `updated_at`) VALUES
(1, 1, '83a67075-c04a-4202-965d-d6c90cfe337f', 'ZDBhOTkyMjUtNTA1ZC00YTYzLTg4MGItYjkwYjA3M2M0MmQx', 'https://play.google.com/store/apps/', '<p><strong>Privacy Policy</strong></p>\r\n\r\n<p style=\"margin-left:0; margin-right:0\">This privacy policy applies to the Whocaller app (hereby referred to as &quot;Application&quot;) for mobile devices that was created by AndroPlaza (hereby referred to as &quot;Service Provider&quot;) as a Free service. This service is intended for use &quot;AS IS&quot;.</p>\r\n\r\n<p><br />\r\n<strong>Information Collection and Use</strong></p>\r\n\r\n<p style=\"margin-left:0; margin-right:0\">The Application collects information when you download and use it. This information may include information such as</p>\r\n\r\n<ul style=\"margin-left:0; margin-right:0\">\r\n	<li>Your device&#39;s Internet Protocol address (e.g. IP address)</li>\r\n	<li>The pages of the Application that you visit, the time and date of your visit, the time spent on those pages</li>\r\n	<li>The time spent on the Application</li>\r\n	<li>The operating system you use on your mobile device</li>\r\n</ul>\r\n\r\n<p style=\"margin-left:0; margin-right:0\">&nbsp;</p>\r\n\r\n<p style=\"margin-left:0; margin-right:0\">The Application does not gather precise information about the location of your mobile device.</p>\r\n\r\n<p style=\"margin-left:0; margin-right:0\">The Service Provider may use the information you provided to contact you from time to time to provide you with important information, required notices and marketing promotions.</p>\r\n\r\n<p style=\"margin-left:0; margin-right:0\">For a better experience, while using the Application, the Service Provider may require you to provide us with certain personally identifiable information, including but not limited to mallikadias16@gmail.com. The information that the Service Provider request will be retained by them and used as described in this privacy policy.</p>\r\n\r\n<p><br />\r\n<strong>Third Party Access</strong></p>\r\n\r\n<p style=\"margin-left:0; margin-right:0\">Only aggregated, anonymized data is periodically transmitted to external services to aid the Service Provider in improving the Application and their service. The Service Provider may share your information with third parties in the ways that are described in this privacy statement.</p>\r\n\r\n<div>&nbsp;\r\n<p style=\"margin-left:0; margin-right:0\">Please note that the Application utilizes third-party services that have their own Privacy Policy about handling data. Below are the links to the Privacy Policy of the third-party service providers used by the Application:</p>\r\n\r\n<ul style=\"margin-left:0; margin-right:0\">\r\n	<li><a href=\"https://www.google.com/policies/privacy/\" rel=\"noopener noreferrer\" style=\"background-color: transparent; box-sizing: inherit; color: #485fc7; cursor: pointer; text-decoration-line: none;\" target=\"_blank\">Google Play Services</a></li>\r\n	<li><a href=\"https://support.google.com/admob/answer/6128543?hl=en\" rel=\"noopener noreferrer\" style=\"background-color: transparent; box-sizing: inherit; color: #485fc7; cursor: pointer; text-decoration-line: none;\" target=\"_blank\">AdMob</a></li>\r\n	<li><a href=\"https://firebase.google.com/support/privacy\" rel=\"noopener noreferrer\" style=\"background-color: transparent; box-sizing: inherit; color: #485fc7; cursor: pointer; text-decoration-line: none;\" target=\"_blank\">Google Analytics for Firebase</a></li>\r\n	<li><a href=\"https://unity3d.com/legal/privacy-policy\" rel=\"noopener noreferrer\" style=\"background-color: transparent; box-sizing: inherit; color: #485fc7; cursor: pointer; text-decoration-line: none;\" target=\"_blank\">Unity</a></li>\r\n	<li><a href=\"https://onesignal.com/privacy_policy\" rel=\"noopener noreferrer\" style=\"background-color: transparent; box-sizing: inherit; color: #485fc7; cursor: pointer; text-decoration-line: none;\" target=\"_blank\">One Signal</a></li>\r\n</ul>\r\n</div>\r\n\r\n<p>&nbsp;</p>\r\n\r\n<p style=\"margin-left:0; margin-right:0\">The Service Provider may disclose User Provided and Automatically Collected Information:</p>\r\n\r\n<p>&nbsp;</p>\r\n\r\n<ul style=\"margin-left:0; margin-right:0\">\r\n	<li>as required by law, such as to comply with a subpoena, or similar legal process;</li>\r\n	<li>when they believe in good faith that disclosure is necessary to protect their rights, protect your safety or the safety of others, investigate fraud, or respond to a government request;</li>\r\n	<li>with their trusted services providers who work on their behalf, do not have an independent use of the information we disclose to them, and have agreed to adhere to the rules set forth in this privacy statement.</li>\r\n</ul>\r\n\r\n<p style=\"margin-left:0; margin-right:0\"><br />\r\n<strong>Opt-Out Rights</strong></p>\r\n\r\n<p style=\"margin-left:0; margin-right:0\">You can stop all collection of information by the Application easily by uninstalling it. You may use the standard uninstall processes as may be available as part of your mobile device or via the mobile application marketplace or network.</p>\r\n\r\n<p><br />\r\n<strong>Data Retention Policy</strong></p>\r\n\r\n<p style=\"margin-left:0; margin-right:0\">The Service Provider will retain User Provided data for as long as you use the Application and for a reasonable time thereafter. If you&#39;d like them to delete User Provided Data that you have provided via the Application, please contact them at mallikadias16@gmail.com and they will respond in a reasonable time.</p>\r\n\r\n<p><br />\r\n<strong>Children</strong></p>\r\n\r\n<p style=\"margin-left:0; margin-right:0\">The Service Provider does not use the Application to knowingly solicit data from or market to children under the age of 13.</p>\r\n\r\n<div>\r\n<p style=\"margin-left:0; margin-right:0\">The Application does not address anyone under the age of 13. The Service Provider does not knowingly collect personally identifiable information from children under 13 years of age. In the case the Service Provider discover that a child under 13 has provided personal information, the Service Provider will immediately delete this from their servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact the Service Provider (mallikadias16@gmail.com) so that they will be able to take the necessary actions.</p>\r\n</div>\r\n\r\n<p><br />\r\n<strong>Security</strong></p>\r\n\r\n<p style=\"margin-left:0; margin-right:0\">The Service Provider is concerned about safeguarding the confidentiality of your information. The Service Provider provides physical, electronic, and procedural safeguards to protect information the Service Provider processes and maintains.</p>\r\n\r\n<p><br />\r\n<strong>Changes</strong></p>\r\n\r\n<p style=\"margin-left:0; margin-right:0\">This Privacy Policy may be updated from time to time for any reason. The Service Provider will notify you of any changes to the Privacy Policy by updating this page with the new Privacy Policy. You are advised to consult this Privacy Policy regularly for any changes, as continued use is deemed approval of all changes.</p>\r\n\r\n<p style=\"margin-left:0; margin-right:0\">This privacy policy is effective as of 2024-08-23</p>\r\n\r\n<p><br />\r\n<strong>Your Consent</strong></p>\r\n\r\n<p style=\"margin-left:0; margin-right:0\">By using the Application, you are consenting to the processing of your information as set forth in this Privacy Policy now and as amended by us.</p>\r\n\r\n<p><br />\r\n<strong>Contact Us</strong></p>\r\n\r\n<p style=\"margin-left:0; margin-right:0\">If you have any questions regarding privacy while using the Application, or have questions about the practices, please contact the Service Provider via email at mallikadias16@gmail.com.</p>', 0, 0, '1', 'Its time to Upgrade to the latest version of our application to get the best experience. oky', 'https://play.google.com/store/apps/', 'mallikadias16@gmail.com', 'AndroPlaza', '+94711920144', 'AndroPlaza', 'AndroPlaza', 'Love this app? Let us Know in the Google Play Store how we can make it even better', NULL, '2024-08-23 13:03:52');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `email_verified_at` timestamp NULL DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `remember_token` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_profiles`
--

CREATE TABLE `user_profiles` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `imgUrl` varchar(255) DEFAULT NULL,
  `isWho` bigint(20) NOT NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ads`
--
ALTER TABLE `ads`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `contacts`
--
ALTER TABLE `contacts`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `failed_jobs`
--
ALTER TABLE `failed_jobs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `failed_jobs_uuid_unique` (`uuid`);

--
-- Indexes for table `migrations`
--
ALTER TABLE `migrations`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  ADD PRIMARY KEY (`email`);

--
-- Indexes for table `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `personal_access_tokens_token_unique` (`token`),
  ADD KEY `personal_access_tokens_tokenable_type_tokenable_id_index` (`tokenable_type`,`tokenable_id`);

--
-- Indexes for table `settings`
--
ALTER TABLE `settings`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `users_email_unique` (`email`);

--
-- Indexes for table `user_profiles`
--
ALTER TABLE `user_profiles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_profiles_email_unique` (`email`),
  ADD UNIQUE KEY `user_profiles_phone_unique` (`phone`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ads`
--
ALTER TABLE `ads`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `contacts`
--
ALTER TABLE `contacts`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `failed_jobs`
--
ALTER TABLE `failed_jobs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `migrations`
--
ALTER TABLE `migrations`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `settings`
--
ALTER TABLE `settings`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `user_profiles`
--
ALTER TABLE `user_profiles`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
