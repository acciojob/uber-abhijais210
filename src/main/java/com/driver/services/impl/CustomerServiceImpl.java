package com.driver.services.impl;

import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		customerRepository2.deleteByCustomerId(customerId);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query

		Driver driver = null;
		List<Driver> drivers = driverRepository2.findAll();

		//sort the Drivers on the basis of id
		drivers.sort(Comparator.comparingInt(Driver::getDriverId));
		for(Driver d : drivers){
			if(d.getCab().getAvailable()){
				driver = d;
				break;
			}
		}
		if(driver == null){
			throw new Exception("No cab available!");
		}
		Customer customer = customerRepository2.findById(customerId).get();


		TripBooking tripBooking = new TripBooking();
		tripBooking.setDistanceInKm(distanceInKm);
		tripBooking.setFromLocation(fromLocation);
		tripBooking.setToLocation(toLocation);
		tripBooking.setStatus(TripStatus.CONFIRMED);
		tripBooking.setDriver(driver);
		tripBooking.setCustomer(customer);

		//add tripBooking in driver and for customer also
		customer.getTripBookingList().add(tripBooking);
		driver.getTripBookingList().add(tripBooking);

		//also update the status for the cab
		driver.getCab().setAvailable(false);
		driverRepository2.save(driver);
		customerRepository2.save(customer);

		return tripBookingRepository2.save(tripBooking);
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();

		//if already completed or cancelled
//		if((tripBooking.getStatus().compareTo(TripStatus.CANCELED) == 0 )||
//				(tripBooking.getStatus().compareTo(TripStatus.COMPLETED) == 0)){
//			return;
//		}
		Driver driver = tripBooking.getDriver();
		driver.getCab().setAvailable(true);
		tripBooking.setStatus(TripStatus.CANCELED);
		tripBooking.setBill(0);
		driverRepository2.save(driver);
		tripBookingRepository2.save(tripBooking);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();

		//if already completed or cancelled
//		if((tripBooking.getStatus().compareTo(TripStatus.CANCELED) == 0 )||
//				(tripBooking.getStatus().compareTo(TripStatus.COMPLETED) == 0)){
//			return;
//		}
		Driver driver = tripBooking.getDriver();
		driver.getCab().setAvailable(true);
		tripBooking.setStatus(TripStatus.COMPLETED);
		tripBooking.setBill(tripBooking.getDistanceInKm()*10);
		driverRepository2.save(driver);
		tripBookingRepository2.save(tripBooking);
	}
}
