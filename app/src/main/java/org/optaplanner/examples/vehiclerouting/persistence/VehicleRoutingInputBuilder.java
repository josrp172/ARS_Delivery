package org.optaplanner.examples.vehiclerouting.persistence;

import org.apache.commons.io.FilenameUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.persistence.TxtInputBuilder;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.AirLocation;
import org.optaplanner.examples.vehiclerouting.domain.location.DistanceType;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaplanner.examples.vehiclerouting.domain.location.segmented.HubSegmentLocation;
import org.optaplanner.examples.vehiclerouting.domain.location.segmented.RoadSegmentLocation;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedDepot;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedVehicleRoutingSolution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VehicleRoutingInputBuilder extends TxtInputBuilder {

    private VehicleRoutingSolution solution;

    private int customerListSize;
    private int vehicleListSize;
    private int capacity;

    private double[] capacity1;
    private long[] vehicleID;

    private Map<Long, Location> locationMap;
    private List<Depot> depotList;

    public Solution readSolution() throws IOException {
        String firstLine = readStringValue();
        if (firstLine.matches("\\s*NAME\\s*:.*")) {
            solution = new VehicleRoutingSolution();
            solution.setId(0L);
            solution.setName(removePrefixSuffixFromLine(firstLine, "\\s*NAME\\s*:", ""));
            readVrpWebFormat();
        } else if (splitBySpacesOrTabs(firstLine).length == 3) {
            solution = new VehicleRoutingSolution();
            solution.setId(0L);
            /* ORIGINAL
            solution.setName(FilenameUtils.getBaseName(inputFile.getName())); */
            solution.setName(FilenameUtils.getBaseName(inputFileNameWithExt));
            String[] tokens = splitBySpacesOrTabs(firstLine, 3);
            customerListSize = Integer.parseInt(tokens[0]);
            vehicleListSize = Integer.parseInt(tokens[1]);
            capacity = Integer.parseInt(tokens[2]);
            readCourseraFormat();
        } else {
            solution = new TimeWindowedVehicleRoutingSolution();
            solution.setId(0L);
            solution.setName(firstLine);
            readTimeWindowedFormat();
        }
         /* TODO enable logger
        BigInteger possibleSolutionSize
                = factorial(customerListSize + vehicleListSize - 1).divide(factorial(vehicleListSize - 1));

        logger.info("VehicleRoutingSolution {} has {} depots, {} vehicles and {} customers with a search space of {}.",
                getInputId(),
                solution.getDepotList().size(),
                solution.getVehicleList().size(),
                solution.getCustomerList().size(),
                getFlooredPossibleSolutionSize(possibleSolutionSize));
        */
        return solution;
    }

    // ************************************************************************
    // CVRP normal format. See http://neo.lcc.uma.es/vrp/
    // ************************************************************************

    public void readVrpWebFormat() throws IOException {
        readVrpWebHeaders();
        readVrpWebLocationList();
        readVrpWebCustomerList();
        readVrpWebDepotList();
        createVrpWebVehicleList();
        readConstantLine("EOF");
    }

    private void readVrpWebHeaders() throws IOException {
        readUntilConstantLine("TYPE *: CVRP");
        customerListSize = readIntegerValue("DIMENSION *:");
        String edgeWeightType = readStringValue("EDGE_WEIGHT_TYPE *:");
        if (edgeWeightType.equalsIgnoreCase("EUC_2D")) {
            solution.setDistanceType(DistanceType.AIR_DISTANCE);
        } else if (edgeWeightType.equalsIgnoreCase("EXPLICIT")) {
            solution.setDistanceType(DistanceType.ROAD_DISTANCE);
            String edgeWeightFormat = readStringValue("EDGE_WEIGHT_FORMAT *:");
            if (!edgeWeightFormat.equalsIgnoreCase("FULL_MATRIX")) {
                throw new IllegalArgumentException("The edgeWeightFormat (" + edgeWeightFormat + ") is not supported.");
            }
        } else if (edgeWeightType.equalsIgnoreCase("SEGMENTED_EXPLICIT")) {
            solution.setDistanceType(DistanceType.SEGMENTED_ROAD_DISTANCE);
            String edgeWeightFormat = readStringValue("EDGE_WEIGHT_FORMAT *:");
            if (!edgeWeightFormat.equalsIgnoreCase("HUB_AND_NEARBY_MATRIX")) {
                throw new IllegalArgumentException("The edgeWeightFormat (" + edgeWeightFormat + ") is not supported.");
            }
        } else {
            throw new IllegalArgumentException("The edgeWeightType (" + edgeWeightType + ") is not supported.");
        }
        solution.setDistanceUnitOfMeasurement(readOptionalStringValue("EDGE_WEIGHT_UNIT_OF_MEASUREMENT *:", "distance"));
        capacity = readIntegerValue("CAPACITY *:");
    }

    private void readVrpWebLocationList() throws IOException {
        DistanceType distanceType = solution.getDistanceType();
        List<HubSegmentLocation> hubLocationList = null;
        locationMap = new LinkedHashMap<Long, Location>(customerListSize);
        if (distanceType == DistanceType.SEGMENTED_ROAD_DISTANCE) {
            int hubListSize= readIntegerValue("HUBS *:");
            hubLocationList = new ArrayList<HubSegmentLocation>(hubListSize);
            readConstantLine("HUB_COORD_SECTION");
            for (int i = 0; i < hubListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpacesOrTabs(line.trim(), 3, 4);
                HubSegmentLocation location = new HubSegmentLocation();
                location.setId(Long.parseLong(lineTokens[0]));
                location.setLatitude(Double.parseDouble(lineTokens[1]));
                location.setLongitude(Double.parseDouble(lineTokens[2]));
                if (lineTokens.length >= 4) {
                    location.setName(lineTokens[3]);
                }
                hubLocationList.add(location);
                locationMap.put(location.getId(), location);
            }
        }
        List<Location> customerLocationList = new ArrayList<Location>(customerListSize);
        readConstantLine("NODE_COORD_SECTION");
        for (int i = 0; i < customerListSize; i++) {
            String line = bufferedReader.readLine();
            String[] lineTokens = splitBySpacesOrTabs(line.trim(), 3, 4);
            Location location;
            switch (distanceType) {
                case AIR_DISTANCE:
                    location = new AirLocation();
                    break;
                case ROAD_DISTANCE:
                    location = new RoadLocation();
                    break;
                case SEGMENTED_ROAD_DISTANCE:
                    location = new RoadSegmentLocation();
                    break;
                default:
                    throw new IllegalStateException("The distanceType (" + distanceType
                            + ") is not implemented.");

            }
            location.setId(Long.parseLong(lineTokens[0]));
            location.setLatitude(Double.parseDouble(lineTokens[1]));
            location.setLongitude(Double.parseDouble(lineTokens[2]));
            if (lineTokens.length >= 4) {
                location.setName(lineTokens[3]);
            }
            customerLocationList.add(location);
            locationMap.put(location.getId(), location);
        }
        if (distanceType == DistanceType.ROAD_DISTANCE) {
            readConstantLine("EDGE_WEIGHT_SECTION");
            for (int i = 0; i < customerListSize; i++) {
                RoadLocation location = (RoadLocation) customerLocationList.get(i);
                Map<RoadLocation, Double> travelDistanceMap = new LinkedHashMap<RoadLocation, Double>(customerListSize);
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpacesOrTabs(line.trim(), customerListSize);
                for (int j = 0; j < customerListSize; j++) {
                    double travelDistance = Double.parseDouble(lineTokens[j]);
                    if (i == j) {
                        if (travelDistance != 0.0) {
                            throw new IllegalStateException("The travelDistance (" + travelDistance
                                    + ") should be zero.");
                        }
                    } else {
                        RoadLocation otherLocation = (RoadLocation) customerLocationList.get(j);
                        travelDistanceMap.put(otherLocation, travelDistance);
                    }
                }
                location.setTravelDistanceMap(travelDistanceMap);
            }
        }
        if (distanceType == DistanceType.SEGMENTED_ROAD_DISTANCE) {
            readConstantLine("SEGMENTED_EDGE_WEIGHT_SECTION");
            int locationListSize = hubLocationList.size() + customerListSize;
            for (int i = 0; i < locationListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpacesOrTabs(line.trim(), 3, null);
                if (lineTokens.length % 2 != 1) {
                    throw new IllegalArgumentException("Invalid SEGMENTED_EDGE_WEIGHT_SECTION line (" + line + ").");
                }
                long id = Long.parseLong(lineTokens[0]);
                Location location = locationMap.get(id);
                if (location == null) {
                    throw new IllegalArgumentException("The location with id (" + id + ") of line (" + line + ") does not exist.");
                }
                Map<HubSegmentLocation, Double> hubTravelDistanceMap = new LinkedHashMap<HubSegmentLocation, Double>(lineTokens.length / 2);
                Map<RoadSegmentLocation, Double> nearbyTravelDistanceMap = new LinkedHashMap<RoadSegmentLocation, Double>(lineTokens.length / 2);
                for (int j = 1; j < lineTokens.length; j += 2) {
                    Location otherLocation = locationMap.get(Long.parseLong(lineTokens[j]));
                    double travelDistance = Double.parseDouble(lineTokens[j + 1]);
                    if (otherLocation instanceof HubSegmentLocation) {
                        hubTravelDistanceMap.put((HubSegmentLocation) otherLocation, travelDistance);
                    } else {
                        nearbyTravelDistanceMap.put((RoadSegmentLocation) otherLocation, travelDistance);
                    }
                }
                if (location instanceof HubSegmentLocation) {
                    HubSegmentLocation hubSegmentLocation = (HubSegmentLocation) location;
                    hubSegmentLocation.setHubTravelDistanceMap(hubTravelDistanceMap);
                    hubSegmentLocation.setNearbyTravelDistanceMap(nearbyTravelDistanceMap);
                } else {
                    RoadSegmentLocation roadSegmentLocation = (RoadSegmentLocation) location;
                    roadSegmentLocation.setHubTravelDistanceMap(hubTravelDistanceMap);
                    roadSegmentLocation.setNearbyTravelDistanceMap(nearbyTravelDistanceMap);
                }
            }
        }
        List<Location> locationList;
        if (distanceType == DistanceType.SEGMENTED_ROAD_DISTANCE) {
            locationList = new ArrayList<Location>(hubLocationList.size() + customerListSize);
            locationList.addAll(hubLocationList);
            locationList.addAll(customerLocationList);
        } else {
            locationList = customerLocationList;
        }
        solution.setLocationList(locationList);
    }

    private void readVrpWebCustomerList() throws IOException {
        readConstantLine("DEMAND_SECTION");
        List<Customer> customerList = new ArrayList<Customer>(customerListSize);
        for (int i = 0; i < customerListSize; i++) {
            String line = bufferedReader.readLine();
            String[] lineTokens = splitBySpacesOrTabs(line.trim(), 2);
            Customer customer = new Customer();
            long id = Long.parseLong(lineTokens[0]);
            customer.setId(id);
            Location location = locationMap.get(id);
            if (location == null) {
                throw new IllegalArgumentException("The customer with id (" + id
                        + ") has no location (" + location + ").");
            }
            customer.setLocation(location);
            int demand = Integer.parseInt(lineTokens[1]);
            customer.setDemand(demand);
            // Notice that we leave the PlanningVariable properties on null
            // Do not add a customer that has no demand
            if (demand != 0) {
                customerList.add(customer);
            }
        }
        solution.setCustomerList(customerList);
    }

    private void readVrpWebDepotList() throws IOException {
        readConstantLine("DEPOT_SECTION");
        depotList = new ArrayList<Depot>(customerListSize);
        long id = readLongValue();
        while (id != -1) {
            Depot depot = new Depot();
            depot.setId(id);
            Location location = locationMap.get(id);
            if (location == null) {
                throw new IllegalArgumentException("The depot with id (" + id
                        + ") has no location (" + location + ").");
            }
            depot.setLocation(location);
            depotList.add(depot);
            id = readLongValue();
        }
        solution.setDepotList(depotList);
    }

    private void createVrpWebVehicleList() throws IOException {
         /* ORIGINAL
        String inputFileName = inputFile.getName(); */
        //String inputFileName = inputFileNameWithExt;
        vehicleListSize = readIntegerValue("VEHICLES *:");
        capacity1 = new double[vehicleListSize];
        vehicleID = new long[vehicleListSize];
        for(int i = 0; i < vehicleListSize; i++){
            String line = bufferedReader.readLine();
            String[] lineTokens = splitBySpacesOrTabs(line.trim(), 2);
            vehicleID[i] = Long.parseLong(lineTokens[0]);
            capacity1[i] = Double.parseDouble(lineTokens[1]);
        }

        /*
        if (inputFileName.toLowerCase().startsWith("tutorial")) {
            vehicleListSize = readIntegerValue("VEHICLES *:");
        } else {
            String inputFileNameRegex = "^.+\\-k(\\d+)\\.vrp$";
            if (!inputFileName.matches(inputFileNameRegex)) {
                throw new IllegalArgumentException("The inputFileName (" + inputFileName
                        + ") does not match the inputFileNameRegex (" + inputFileNameRegex + ").");
            }
            String vehicleListSizeString = inputFileName.replaceAll(inputFileNameRegex, "$1");
            try {
                vehicleListSize = Integer.parseInt(vehicleListSizeString);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The inputFileName (" + inputFileName
                        + ") has a vehicleListSizeString (" + vehicleListSizeString + ") that is not a number.", e);
            }
        }
        */
        createVehicleList();
    }

    private void createVehicleList() {
        List<Vehicle> vehicleList = new ArrayList<Vehicle>(vehicleListSize);
        for (int i = 0; i < vehicleListSize; i++) {
            Vehicle vehicle = new Vehicle();
            vehicle.setId(vehicleID[i]);
            vehicle.setCapacity(capacity1[i]);
            vehicle.setDepot(depotList.get(0));
            vehicleList.add(vehicle);
        }
        solution.setVehicleList(vehicleList);
    }

    // ************************************************************************
    // CVRP coursera format. See https://class.coursera.org/optimization-001/
    // ************************************************************************

    public void readCourseraFormat() throws IOException {
        solution.setDistanceType(DistanceType.AIR_DISTANCE);
        solution.setDistanceUnitOfMeasurement("distance");
        List<Location> locationList = new ArrayList<Location>(customerListSize);
        depotList = new ArrayList<Depot>(1);
        List<Customer> customerList = new ArrayList<Customer>(customerListSize);
        locationMap = new LinkedHashMap<Long, Location>(customerListSize);
        for (int i = 0; i < customerListSize; i++) {
            String line = bufferedReader.readLine();
            String[] lineTokens = splitBySpacesOrTabs(line.trim(), 3, 4);
            AirLocation location = new AirLocation();
            location.setId((long) i);
            location.setLatitude(Double.parseDouble(lineTokens[1]));
            location.setLongitude(Double.parseDouble(lineTokens[2]));
            if (lineTokens.length >= 4) {
                location.setName(lineTokens[3]);
            }
            locationList.add(location);
            if (i == 0) {
                Depot depot = new Depot();
                depot.setId((long) i);
                depot.setLocation(location);
                depotList.add(depot);
            } else {
                Customer customer = new Customer();
                customer.setId((long) i);
                customer.setLocation(location);
                int demand = Integer.parseInt(lineTokens[0]);
                customer.setDemand(demand);
                // Notice that we leave the PlanningVariable properties on null
                // Do not add a customer that has no demand
                if (demand != 0) {
                    customerList.add(customer);
                }
            }
        }
        solution.setLocationList(locationList);
        solution.setDepotList(depotList);
        solution.setCustomerList(customerList);
        createVehicleList();
    }

    // ************************************************************************
    // CVRPTW normal format. See http://neo.lcc.uma.es/vrp/
    // ************************************************************************

    public void readTimeWindowedFormat() throws IOException {
        readTimeWindowedHeaders();
        readTimeWindowedDepotAndCustomers();
        createVehicleList();
    }

    private void readTimeWindowedHeaders() throws IOException {
        solution.setDistanceType(DistanceType.AIR_DISTANCE);
        solution.setDistanceUnitOfMeasurement("distance");
        readEmptyLine();
        readConstantLine("VEHICLE");
        readConstantLine("NUMBER +CAPACITY");
        String[] lineTokens = splitBySpacesOrTabs(readStringValue(), 2);
        vehicleListSize = Integer.parseInt(lineTokens[0]);
        capacity = Integer.parseInt(lineTokens[1]);
        readEmptyLine();
        readConstantLine("CUSTOMER");
        readConstantLine("CUST\\s+NO\\.\\s+XCOORD\\.\\s+YCOORD\\.\\s+DEMAND\\s+READY\\s+TIME\\s+DUE\\s+DATE\\s+SERVICE\\s+TIME");
        readEmptyLine();
    }

    private void readTimeWindowedDepotAndCustomers() throws IOException {
        String line = bufferedReader.readLine();
        int locationListSizeEstimation = 25;
        List<Location> locationList = new ArrayList<Location>(locationListSizeEstimation);
        depotList = new ArrayList<Depot>(1);
        TimeWindowedDepot depot = null;
        List<Customer> customerList = new ArrayList<Customer>(locationListSizeEstimation);
        boolean first = true;
        while (line != null && !line.trim().isEmpty()) {
            String[] lineTokens = splitBySpacesOrTabs(line.trim(), 7);
            long id = Long.parseLong(lineTokens[0]);
            AirLocation location = new AirLocation();
            location.setId(id);
            location.setLatitude(Double.parseDouble(lineTokens[1]));
            location.setLongitude(Double.parseDouble(lineTokens[2]));
            locationList.add(location);
            int demand = Integer.parseInt(lineTokens[3]);
            int readyTime = Integer.parseInt(lineTokens[4]) * 1000;
            int dueTime = Integer.parseInt(lineTokens[5]) * 1000;
            int serviceDuration = Integer.parseInt(lineTokens[6]) * 1000;
            if (first) {
                depot = new TimeWindowedDepot();
                depot.setId(id);
                depot.setLocation(location);
                if (demand != 0) {
                    throw new IllegalArgumentException("The depot with id (" + id
                            + ") has a demand (" + demand + ").");
                }
                depot.setReadyTime(readyTime);
                depot.setDueTime(dueTime);
                if (serviceDuration != 0) {
                    throw new IllegalArgumentException("The depot with id (" + id
                            + ") has a serviceDuration (" + serviceDuration + ").");
                }
                depotList.add(depot);
                first = false;
            } else {
                TimeWindowedCustomer customer = new TimeWindowedCustomer();
                customer.setId(id);
                customer.setLocation(location);
                customer.setDemand(demand);
                customer.setReadyTime(readyTime);
                // Score constraint arrivalAfterDueTimeAtDepot is a build-in hard constraint in VehicleRoutingImporter
                int maximumDueTime = depot.getDueTime()
                        - serviceDuration - location.getDistance(depot.getLocation());
                if (dueTime > maximumDueTime) {
                    /* TODO enable logger
                    logger.warn("The customer ({})'s dueTime ({}) was automatically reduced" +
                                    " to maximumDueTime ({}) because of the depot's dueTime ({}).",
                            customer, dueTime, maximumDueTime, depot.getDueTime());
                    */
                    dueTime = maximumDueTime;
                }
                customer.setDueTime(dueTime);
                customer.setServiceDuration(serviceDuration);
                // Notice that we leave the PlanningVariable properties on null
                // Do not add a customer that has no demand
                if (demand != 0) {
                    customerList.add(customer);
                }
            }
            line = bufferedReader.readLine();
        }
        solution.setLocationList(locationList);
        solution.setDepotList(depotList);
        solution.setCustomerList(customerList);
        customerListSize = locationList.size();
    }

}
