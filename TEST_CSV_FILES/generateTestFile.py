import argparse
import random


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('fileName', type=str, nargs=1, help='the filename to save generated file as')
    parser.add_argument('noOfVehicles', type=int, nargs=1, help='the number of vehicle spawns to be generated')
    parser.add_argument('disabledVehicles', type=float, nargs=1, help='the percentage of disabled vehicles to spawn')
    parser.add_argument('automatedVehicles', type=float, nargs=1, help='the percentage of automated vehicles to spawn')
    parser.add_argument('parkingTime', type=str, nargs=1, help='the parking time for each vehicle: Randomly generated (r), or constant (specify time)')
    parser.add_argument('specTypes', type=str, nargs='+', help='the types of vehicles to spawn')
    args = parser.parse_args()

    f = open(args.fileName[0], "w")
    f.write("Spec,Disabled,Automated,Entry,Parking\n")

    print(args)

    spawnTime = 10
    for i in range(args.noOfVehicles[0]):
        if (args.parkingTime[0] == 'r'):
            parkingTime = str(generate_parking_time())
        else:
            parkingTime = args.parkingTime[0]
	automated = get_is_automated(float(args.automatedVehicles[0])/100);
        f.write(get_random_spec(args.specTypes) + "," +
                get_is_disabled(float(args.disabledVehicles[0])/100,automated == "Y") + "," +
                automated + "," +
                str(spawnTime) + "," +
                parkingTime + "\n")
        spawnTime+=10
    f.close()

def get_random_spec(specs):
    proportion = 1.0/len(specs)
    rand = random.random()
    i = proportion
    for spec in specs:
        if (rand<i):
            return spec
        else:
            i += proportion

def get_is_disabled(disabledProportion, isAutomated):
    rand = random.random()
    if (not isAutomated):
        if (rand<disabledProportion):
            return "Y"
        else:
            return "N"
    else:
        return "N/A"

def get_is_automated(automatedProportion):
    rand = random.random()
    if (rand<automatedProportion):
        return "Y"
    else:
        return "N"

def generate_parking_time():
    return int((random.random()*18000)+2000)


main()
