# Software Test Plan - LibTek Regression Cycle

## 1. Project Pre-conditions
- MySQL Database is active and contains the `libtek` schema.
- Spring Boot Backend is running on `http://localhost:8080`.
- React Frontend is running on `http://localhost:3000`.

## 2. Functional Requirements Coverage
| Requirement ID | Description | Status |
|---|---|---|
| FR-01 | Admin Authentication | Implemented |
| FR-02 | Resource Management (CRUD) | Implemented |
| FR-03 | Dashboard Statistics | Implemented |
| FR-04 | Library Scanner Simulator | Implemented |

## 3. Detailed Test Cases

### TC-01: Admin Authentication
- **Test Objective:** Verify that the admin can access the protected dashboard.
- **Steps:** 1. Navigate to `http://localhost:3000/admin/login`.
  2. Enter Email: `admin@libtek.edu`.
  3. Enter Password: `admin123`.
  4. Click "Login" button.
- **Expected Result:** App redirects to `/admin/dashboard` and displays the sidebar.

### TC-02: Resource Management (Add Book)
- **Test Objective:** Verify the Vertical Slice connection for the Resource module.
- **Steps:** 1. Navigate to the "Resources" tab.
  2. Click "Add Resource".
  3. Fill in: Name="Clean Code", Author="Robert Martin", Type="BOOK".
  4. Click "Save".
- **Expected Result:** The new book appears in the table; "201 Created" status in Network tab.

### TC-03: Scanner Simulation & Occupancy Update
- **Test Objective:** Verify real-time data flow between Scanner and Dashboard slices.
- **Steps:** 1. Open the Dashboard.
  2. Locate the "Scanner Simulator" input.
  3. Enter Student ID: `2024-TEST-01`.
  4. Click "Simulate Scan".
- **Expected Result:** The "Current Occupancy" count increments by 1; graph pulses to show update.

## 4. Automated Test Strategy
- **Tool:** Postman API Client.
- **Verification Points:**
  - `GET /api/dashboard/summary`: Ensure JSON returns valid integers for occupancy.
  - `POST /api/scanner/scan`: Ensure JSON returns a `LibraryVisit` object with `status: "IN_LIBRARY"`.